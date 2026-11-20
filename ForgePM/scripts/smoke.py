#!/usr/bin/env python3
"""Run the auth lifecycle through the web server using only the Python standard library."""
import json
import sys
from urllib.error import HTTPError
from urllib.request import Request, urlopen
from uuid import uuid4

base = (sys.argv[1] if len(sys.argv) > 1 else 'http://localhost:8080').rstrip('/') + '/api/v1'
email = f'smoke-{uuid4()}@example.com'
credentials = {'email': email, 'password': 'local smoke test password'}

def call(path, expected, data=None, token=None):
    headers = {'Content-Type': 'application/json'} if data else {}
    if token:
        headers['Authorization'] = f'Bearer {token}'
    request = Request(base + path, json.dumps(data).encode() if data else None, headers)
    try:
        response = urlopen(request, timeout=10)
    except HTTPError as error:
        response = error
    with response:
        if response.code != expected:
            raise RuntimeError(f'{path}: expected {expected}, got {response.code}')
        return json.loads(response.read()) if 200 <= expected < 300 else None

call('/users/me', 401)
call('/auth/register', 201, {**credentials, 'fullName': 'Smoke User'})
call('/auth/register', 409, {**credentials, 'fullName': 'Smoke User'})
call('/auth/login', 401, {**credentials, 'password': 'wrong password'})
session = call('/auth/login', 200, credentials)
profile = call('/users/me', 200, token=session['accessToken'])
if profile['email'] != email or 'passwordHash' in profile:
    raise RuntimeError('Unexpected profile response')
print('Auth smoke test passed through the web server.')
