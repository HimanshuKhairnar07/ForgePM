import {useState, type FormEvent} from 'react';
import {createRoot} from 'react-dom/client';
import {BrowserRouter, Link, Navigate, Route, Routes, useNavigate} from 'react-router-dom';
import {QueryClient, QueryClientProvider, useQuery} from '@tanstack/react-query';
import './style.css';

type Profile = {id:string; fullName:string; email:string};
class ApiError extends Error { constructor(public status:number, message:string){super(message);} }
async function api<T>(path:string, body?:object, token?:string):Promise<T>{
  const res=await fetch(`/api/v1${path}`,{method:body?'POST':'GET',headers:{...(body?{'Content-Type':'application/json'}:{}),...(token?{Authorization:`Bearer ${token}`}:{})},body:body?JSON.stringify(body):undefined});
  if(!res.ok){const error=await res.json().catch(()=>null); throw new ApiError(res.status,error?.detail || (res.status>=500?'The service is unavailable. Try again shortly.':'The request could not be completed.'));}
  return res.json();
}
const client=new QueryClient({defaultOptions:{queries:{retry:false}}});
function App(){
  const [token,setToken]=useState<string>();
  function logout(){client.clear();setToken(undefined);}
  return <main className="layout"><aside className="story"><div className="brand"><span>F</span> ForgePM</div><div className="pitch"><p className="tag">TEAM PROJECT MANAGEMENT</p><h1>Give your team’s<br/>work a home.</h1><p>Track issues, see what’s in progress, and keep decisions with the work they belong to.</p><div className="sample" aria-hidden="true"><header>EXAMPLE BOARD <span>↗</span></header><div className="columns">{['To do','In progress','Done'].map((s,i)=><div key={s}><small><i className={`dot d${i}`}/>{s}</small><div className="card"><b>{['Define scope','Build the API','Set up repo'][i]}</b><span>FP-{i+1}</span></div></div>)}</div></div></div><footer>FORGEPM <span> / </span> FIRST IMPLEMENTATION SPRINT</footer></aside><section className="content"><Routes><Route path="/login" element={token?<Navigate to="/account" replace/>:<Auth key="login" register={false} onToken={setToken}/>}/><Route path="/register" element={token?<Navigate to="/account" replace/>:<Auth key="register" register onToken={setToken}/>}/><Route path="/account" element={token?<Account token={token} logout={logout}/>:<Navigate to="/login" replace/>}/><Route path="*" element={<Navigate to="/login" replace/>}/></Routes></section></main>
}
function Auth({register,onToken}:{register:boolean;onToken:(t:string)=>void}){
  const navigate=useNavigate(); const [busy,setBusy]=useState(false); const [error,setError]=useState('');
  async function submit(e:FormEvent<HTMLFormElement>){
    e.preventDefault();setError('');setBusy(true);
    const data=new FormData(e.currentTarget); const password=String(data.get('password'));const email=String(data.get('email')).trim();
    try{
      if(new TextEncoder().encode(password).length>72) throw new Error('Use a password of at most 72 UTF-8 bytes.');
      if(register){await api('/auth/register',{fullName:data.get('fullName'),email,password});navigate('/login',{state:{registered:true}});}
      else {const session=await api<{accessToken:string}>('/auth/login',{email,password});onToken(session.accessToken);navigate('/account');}
    }catch(err){setError(err instanceof TypeError?'Cannot reach the server. Try again shortly.':err instanceof Error?err.message:'Something went wrong.');}finally{setBusy(false);}
  }
  return <div className="form-shell"><p className="eyebrow">YOUR FORGEPM ACCOUNT</p><h2>{register?'Create an account':'Welcome back'}</h2><p className="muted">{register?'Enter your details to get started.':'Sign in to access your account.'}</p>{!register&&<Registered/>}<form onSubmit={submit}>{register&&<label>Full name<input name="fullName" autoComplete="name" maxLength={120} placeholder="Alex Morgan" required/></label>}<label>Email address<input name="email" autoComplete="email" type="email" maxLength={254} placeholder="you@example.com" required/></label><label>Password<input name="password" type="password" autoComplete={register?'new-password':'current-password'} minLength={register?12:undefined} maxLength={72} placeholder={register?'At least 12 characters':'Enter your password'} required/></label>{error&&<p role="alert" className="error">{error}</p>}<button disabled={busy}>{busy?'Please wait…':register?'Create account':'Sign in'} <span>→</span></button></form><p className="switch">{register?'Already have an account?':'New to ForgePM?'} <Link to={register?'/login':'/register'}>{register?'Sign in':'Create an account'}</Link></p></div>
}
import {useLocation} from 'react-router-dom';
function Registered(){return useLocation().state?.registered?<p role="status" className="notice">Account created. Sign in with your new credentials.</p>:null;}
function Account({token,logout}:{token:string;logout:()=>void}){
  const profile=useQuery({queryKey:['profile'],queryFn:()=>api<Profile>('/users/me',undefined,token)});
  return <div className="form-shell"><p className="eyebrow">ACCOUNT OVERVIEW</p><h2>{profile.data?`Hi, ${profile.data.fullName}`:'Your account'}</h2>{profile.isPending&&<p role="status">Loading your profile…</p>}{profile.isError&&<p role="alert" className="error">{profile.error instanceof ApiError&&profile.error.status===401?'Your session is no longer valid. Sign out and sign in again.':'Could not load your profile.'}</p>}{profile.data&&<><p className="muted">Signed in as {profile.data.email}</p><p className="notice">Workspace creation and project tools are scheduled for later sprints.</p></>}<button onClick={logout}>Sign out <span>→</span></button></div>
}

createRoot(document.getElementById('root')!).render(<QueryClientProvider client={client}><BrowserRouter><App/></BrowserRouter></QueryClientProvider>);
