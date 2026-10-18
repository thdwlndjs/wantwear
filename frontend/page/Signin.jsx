import React, { useState } from 'react';
import axios from 'axios';
import './Signin.css';

const Signin = () => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');

    const [errorMsg, setErrorMsg] = useState('');
    const [successMsg, setSuccessMsg] = useState('');

    const handleSignUp = async (e) => {
        e.preventDefault();
        setErrorMsg('');
        setSuccessMsg('');

        try {
            const res = await axios.post("http://localhost:8080/api/auth/signin", {
                email,
                password
            });

            if (res.data.success) {
                setSuccessMsg("회원가입 성공!");
            } else {
                setErrorMsg("이미 존재하는 이메일입니다.");
            }

        } catch (err) {
            console.error(err);
            setErrorMsg("회원가입 실패");
        }
    };

    return (
        <div className="signin-box">
            <div className="content">
                <h1>Sign in</h1>
                <form onSubmit={handleSignUp}>
                    <input
                        type="text"
                        placeholder="이메일"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                    />
                    <input
                        type="password"
                        placeholder="비밀번호"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                    />
                    <button type="submit" className="login-button">Join</button>

                    {errorMsg && <p style={{ color: 'red' }}>{errorMsg}</p>}
                    {successMsg && <p style={{ color: 'green' }}>{successMsg}</p>}
                </form>
            </div>
        </div>
    );
};

export default Signin;
