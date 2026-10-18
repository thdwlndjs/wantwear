import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { GoPlusCircle, GoDash } from 'react-icons/go';
import { AiOutlineClose } from 'react-icons/ai';
import { useParams } from 'react-router-dom';
import he from 'he';

import './Home.css';

axios.defaults.withCredentials = true; // JWT 쿠키 보내기

const menuList = ['Outer', 'Top', 'Bottoms', 'ACC', 'Shoes', 'Logout'];
const ITEMS_PER_PAGE = 7;

function Home() {
  const { category: Param } = useParams();
  const [products, setProducts] = useState([]);
  const [url, seturl] = useState('');
  const [category, setCategory] = useState('');
  const [modalOpen, setModalOpen] = useState(false);
  const [categoryFilter, setCategoryFilter] = useState(null);
  const [currentPage, setCurrentPage] = useState(1);

  // 1. 최초 상품 목록 불러오기
  const loadProducts = async () => {
    try {
      const res = await axios.get('http://localhost:8080/api/products');
      setProducts(res.data);
    } catch (err) {
      console.error('상품 목록 불러오기 실패:', err);
    }
  };

  useEffect(() => {
    loadProducts();
  }, []);

  // 2. URL로부터 og:image, og:title 가져오기 (Spring Boot)
  const fetchOgData = async (url) => {
    try {
      const res = await axios.get(
        `http://localhost:8080/api/preview?url=${encodeURIComponent(url)}`
      );

      return {
        url: url,
        ogImage: res.data.ogImage || '/fallback.png',
        ogTitle: res.data.ogTitle || '제목 없음'
      };
    } catch (error) {
      console.error('[X] OG 데이터 오류:', error);
      return {
        url: url,
        ogImage: '/fallback.png',
        ogTitle: '데이터를 불러올 수 없습니다.'
      };
    }
  };

  // 3. 상품 추가 (DB 저장)
  const onAdd = async () => {
    if (!url.trim() || !category) return alert('링크와 카테고리를 모두 입력해주세요.');

    try {
      const ogData = await fetchOgData(url);
      //품절 여부 확인 API 호출 (Spring Boot)
      const soldOutRes = await axios.get(
        `http://localhost:8080/api/soldout/check?url=${encodeURIComponent(url)}`
      );

      console.log('[soldout api]', soldOutRes.data);
      console.log('[soldout api type]', typeof soldOutRes.data?.isSoldOut, soldOutRes.data?.isSoldOut);

      const isSoldOut = soldOutRes.data?.isSoldOut ?? false;
      const newItem = {
        url: ogData.url,
        ogImage: ogData.ogImage,
        ogTitle: ogData.ogTitle,
        category,
        soldOut: isSoldOut
      };

      // DB 저장
      const res = await axios.post('http://localhost:8080/api/products', newItem);

      console.log('[products post response]', res.data);

      // 화면 갱신
      setProducts(prev => [{ ...res.data, soldOut: isSoldOut }, ...prev]);

      seturl('');
      setCategory('');
      setModalOpen(false);
    } catch (e) {
      console.error('[X] 상품 추가 실패:', e);
      alert('상품 추가에 실패했습니다.');
    }
  };

  // 4. 상품 삭제 (DB 삭제)
  const handleDelete = async (id) => {
    if (!window.confirm('삭제하시겠습니까?')) return;

    try {
      await axios.delete(`http://localhost:8080/api/products/${id}`);
      setProducts(prev => prev.filter(item => item.id !== id));
    } catch (err) {
      console.error('상품 삭제 실패:', err);
      alert('삭제 실패');
    }
  };

  // 5. 필터링 + 페이지네이션
  useEffect(() => {
    if (Param && menuList.includes(Param)) {
      setCategoryFilter(Param);
    } else {
      setCategoryFilter(null);
    }
  }, [Param]);

  const filteredProducts = categoryFilter
    ? products.filter(product => product.category === categoryFilter)
    : products;

  const totalPages = Math.ceil(filteredProducts.length / ITEMS_PER_PAGE);
  const paginatedProducts = filteredProducts.slice(
    (currentPage - 1) * ITEMS_PER_PAGE,
    currentPage * ITEMS_PER_PAGE
  );

  return (
    <div>
      {/* 카테고리 필터 */}
      <div className="category-filter"></div>

      {/* 상품 리스트 */}
      <ul>
        {paginatedProducts.map(product => (
          <li className={`product-item ${product.isSoldOut ? 'sold-out' : ''}`} key={product.id}>
            <button className="p-close-btn" onClick={() => {
              if (window.confirm('삭제하시겠습니까?')) {
                handleDelete(product.id);
              }
            }}>
              <GoDash size={20} />
            </button>
            <a href={product.url} target="_blank" rel="noopener noreferrer">
              <img src={product.ogImage} alt="썸네일" width="550px" />
            </a>
            <h2 className="product-title">{he.decode(product.ogTitle)}</h2>
          </li>
        ))}
      </ul>

      {/* 페이지네이션 */}
      <div className="pagination">
        {[...Array(totalPages)].map((_, idx) => (
          <button
            key={idx}
            onClick={() => setCurrentPage(idx + 1)}
            className={currentPage === idx + 1 ? 'active' : ''}
          >
            {idx + 1}
          </button>
        ))}
      </div>

      {/* 추가 버튼 */}
      <button className="fixed-button" onClick={() => setModalOpen(true)}>
        <GoPlusCircle size={40} />
      </button>

      {/* 모달 */}
      {modalOpen && (
        <div classname="modal-overlay">
          <div className="modal">
            <div className="modal-header">
              <h2>상품 등록</h2>
              <button className="close-btn" onClick={() => setModalOpen(false)}>
                <AiOutlineClose size={20} />
              </button>
            </div>

            <div className="modal-body">
              <input
                type="text"
                value={url}
                onChange={e => seturl(e.target.value)}
                placeholder="상품 URL 입력"
              />

              <select value={category} onChange={e => setCategory(e.target.value)}>
                <option value="">카테고리 선택</option>
                {menuList.filter(cat => cat !== 'Logout').map(cat => (
                  <option key={cat} value={cat}>{cat}</option>
                ))}
              </select>

              <button className="add-button" onClick={onAdd}>추가</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default Home;
