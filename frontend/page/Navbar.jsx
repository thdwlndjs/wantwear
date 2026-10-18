import { useNavigate } from 'react-router-dom';
import { logout } from '../api/authApi'

const Navbar = ({ setAuth }) => {
  const menuList = ['Outer', 'Top', 'Bottoms', 'ACC', 'Shoes', 'Logout'];
  const navigate = useNavigate();


  const handleClick = async (menu) => {
    if (menu === 'Logout') {
      try{
        await logout();
        setAuth({
        loading: false,
        loggedIn: false,
      }); 
      navigate('/',{replace: true});
    }catch(e) {
          console.error('로그아웃 실패', e);
        }
    } else {
      navigate(`/home/${encodeURIComponent(menu)}`);
    }
  };

  return (
    <div>
      <div className='menu-area'>
        <ul className='menu-list'>
          {menuList.map((menu, index) => (
                   <li key={index} onClick={() => handleClick(menu)}>
              {menu}
            </li>
          ))}
        </ul>
      </div>
    </div>
  );
};

export default Navbar;
