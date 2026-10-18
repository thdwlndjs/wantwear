import axios from 'axios';

/*
 * 원본 참고:
 * clothes/src/page/Login.jsx
 * clothes/src/page/Navbar.jsx
 * clothes/src/App.jsx
 *
 * 핵심:
 * backend가 JWT를 HttpOnly 쿠키로 내려주므로,
 * 프론트는 token을 localStorage에 저장하지 않습니다.
 *
 * HttpOnly 쿠키는 JavaScript로 읽을 수 없습니다.
 * 대신 브라우저가 자동 저장/자동 전송합니다.
 *
 * 그러려면 axios 요청마다 withCredentials: true가 필요합니다.
 */

const api = axios.create({
  baseURL: 'http://localhost:8080',
  withCredentials: true,
});

export async function login(email, password) {
  /*
   * 성공하면 서버 응답의 Set-Cookie 헤더로 jwt 쿠키가 저장됩니다.
   * JavaScript에서는 그 쿠키 값을 직접 읽지 않습니다.
   */
  const res = await api.post('/api/auth/login', { email, password });
  return res.data;
}

export async function logout() {
  /*
   * 서버가 jwt 쿠키를 maxAge=0으로 다시 내려주면 브라우저 쿠키가 삭제됩니다.
   */
  const res = await api.post('/api/auth/logout');
  return res.data;
}

export async function getAuthStatus() {
  /*
   * 페이지 새로고침 후에도 쿠키가 남아 있으면
   * 서버가 loggedIn=true를 반환합니다.
   */
  const res = await api.get('/api/auth/status');
  return res.data;
}

