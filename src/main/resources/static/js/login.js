async function loginUser(event) {
    event.preventDefault();

    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;

    try {
        const response = await fetch('/users/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password })
        });

        const result = await response.json();

        if (response.ok && result.accessToken) {
            const accessToken = result.accessToken;
            const refreshToken = result.refreshToken;
            localStorage.setItem('accessToken', accessToken);  // JWT 토큰 저장
            localStorage.setItem('refreshToken', refreshToken);

            // 토큰에서 ADMIN 역할을 확인
            const isAdmin = checkUserRole(accessToken);
            if (isAdmin) {
                await loadAdminPage(accessToken);  // ADMIN일 경우에만 admin 페이지 로드
            } else {
                alert("관리자 권한이 필요합니다.");
                localStorage.removeItem('accessToken');  // 권한이 없을 경우 토큰 삭제
                localStorage.removeItem('refreshToken');
            }
        } else {
            alert("로그인 실패! 이메일과 비밀번호를 확인하세요.");
        }
    } catch (error) {
        console.error("로그인 중 오류 발생:", error);
        alert("로그인 중 오류가 발생했습니다. 다시 시도해 주세요.");
    }
}

// 역할 확인 함수
function checkUserRole(token) {
    try {
        const payload = JSON.parse(atob(token.split('.')[1])); // JWT의 payload 부분을 디코딩
        return payload.role === 'ADMIN'; // 역할이 ADMIN인지 확인
    } catch (error) {
        console.error("토큰 파싱 오류:", error);
        return false;
    }
}

// 관리자 페이지 로드 함수
async function loadAdminPage(token) {
    try {
        const response = await fetch('/auth/admin', {
            method: 'GET',
            headers: { 'Authorization': `Bearer ${token}` }
        });

        if (response.ok) {
            const html = await response.text();
            document.open();
            document.write(html);
            document.close();
        } else {
            console.error("관리자 페이지 요청 실패:", response.status);
            alert("관리자 페이지 접근이 거부되었습니다.");
        }
    } catch (error) {
        console.error("관리자 페이지 로드 중 오류:", error);
    }
}
