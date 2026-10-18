import React, { useState, useRef } from 'react';
import Login from './Login';
import Signin from './Signin';

import './FirstNav.css';

function FirstNav({ setAuth }) {
  const menuList = ['Log in', 'Sign in'];
  const [modalType, setModalType] = useState(null); // 'login' | 'signin'
  const [isModalOpen, setIsModalOpen] = useState(false);
  const modalContentRef = useRef(null);

  const handleMenuClick = (menu) => {
    if (menu === 'Log in') {
      setModalType('login');
    } else if (menu === 'Sign in') {
      setModalType('signin');
    }
    setIsModalOpen(true);
  };

  // 모달 배경 클릭 핸들러
  const handleOverlayClick = (e) => {
    // 모달 내부 컨텐츠 영역 클릭은 무시
    if (modalContentRef.current && !modalContentRef.current.contains(e.target)) {
      setIsModalOpen(false);
    }
  };

  return (
    <div>
      <div className="menu-area">
        <ul className="menu-list">
          {menuList.map((menu, index) => (
            <li
              key={index}
              onClick={() => handleMenuClick(menu)}
              style={{
                cursor: ['Log in', 'Sign in'].includes(menu) ? 'pointer' : 'default',
              }}
            >
              {menu}
            </li>
          ))}
        </ul>
      </div>

      {isModalOpen && (
        <div className="modal-overlay" onClick={handleOverlayClick}>
          <div ref={modalContentRef}>
            {modalType === 'login' && <Login setAuth={setAuth} />}
            {modalType === 'signin' && <Signin />}
          </div>
        </div>
      )}
    </div>
  );
}

export default FirstNav;
