// App.jsx
import React, { useEffect, useState } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate, Outlet } from 'react-router-dom';
import axios from 'axios';
import FirstNav from './page/FirstNav';
import Navbar from './page/Navbar';
import Home from './page/Home';
import './App.css';

function RequireAuth({ auth }) {
  if (auth.loading) return null; // 또는 로딩 UI
  if (!auth.loggedIn) return <Navigate to="/" replace />;
  return <Outlet />;
}

function PublicOnly({ auth }) {
  if (auth.loading) return null;
  if (auth.loggedIn) return <Navigate to="/Home" replace />;
  return <Outlet />;
}

export default function App() {
  const [auth, setAuth] = useState({ loading: true, loggedIn: false });

  useEffect(() => {
    const checkAuth = async () => {
      try {
        const res = await axios.get("/api/auth/status", {
          withCredentials: true,
        });

        setAuth({
          loading: false,
          loggedIn: !!res.data.loggedIn,
        });

      } catch (e) {
        console.error(e);

        setAuth({
          loading: false,
          loggedIn: false,
        });
      }
    }; 

    checkAuth();
  }, []);

  return (
    <Router>
      <Routes>
        {/* 비로그인 전용 */}
        <Route element={<PublicOnly auth={auth} />}>
          <Route path="/" element={<FirstNav setAuth={setAuth} />} />
        </Route>

        {/* 로그인 사용자 전용 */}
        <Route element={<RequireAuth auth={auth} />}>
          <Route path="/Home" element={<><Navbar setAuth={setAuth} /><Home /></>} />
          <Route path="/Home/:category" element={<><Navbar setAuth={setAuth} /><Home /></>} />
        </Route>

        {/* 404 */}
        <Route path="*" element={<div>404 Not Found</div>} />
      </Routes>
    </Router>
  );
}
