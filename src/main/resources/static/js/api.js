window.api = {
    async fetch(url, options = {}) {
        const token = sessionStorage.getItem('au');
        const isFormData = options.body instanceof FormData;

        const headers = {
            ...(!isFormData && { 'Content-Type': 'application/json' }),
            ...options.headers
        };

        if (token) {
            headers['Authorization'] = token;
        }

        const response = await fetch(url, { ...options, headers });

        const newToken = response.headers.get('Authorization');

        if (newToken && newToken.startsWith('Bearer ')) {
            sessionStorage.setItem('au', newToken);
            console.log('토큰이 자동 갱신되었습니다.');
        }

        if (response.status === 401) {
            sessionStorage.removeItem('au');
            location.href = '/login';
        }

        return response;
    }
}