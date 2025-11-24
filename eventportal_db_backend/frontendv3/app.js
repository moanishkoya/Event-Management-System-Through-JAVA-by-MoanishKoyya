// CONFIGURATION
const API_BASE_URL = 'http://localhost:8080';

// STATE
let currentUser = null;
let authToken = localStorage.getItem('token');

// DOM ELEMENTS
const navLinks = document.querySelectorAll('.nav-item');
const sections = document.querySelectorAll('.page-section');
const authModal = document.getElementById('authModal');
const loginBtn = document.getElementById('loginBtn');
const registerBtn = document.getElementById('registerBtn');
const closeModal = document.getElementById('closeModal');
const loginForm = document.getElementById('loginForm');
const registerForm = document.getElementById('registerForm');
const loginContainer = document.getElementById('loginFormContainer');
const registerContainer = document.getElementById('registerFormContainer');
const switchRegister = document.getElementById('showRegister');
const switchLogin = document.getElementById('showLogin');
const authButtons = document.getElementById('authButtons');
const userMenu = document.getElementById('userMenu');
const welcomeMsg = document.getElementById('welcomeMsg');
const logoutBtn = document.getElementById('logoutBtn');
const dashboardLink = document.getElementById('dashboardLink');
const toast = document.getElementById('toast');
const themeToggle = document.getElementById('themeToggle');

// --- INITIALIZATION ---
document.addEventListener('DOMContentLoaded', () => {
    initTheme();
    checkAuth();
    setupNavigation();
    setupForms();
    loadEvents(); 
});

// --- THEME LOGIC (RETRO EDITION) ---
function initTheme() {
    const savedTheme = localStorage.getItem('theme');
    const icon = themeToggle.querySelector('i');
    
    // Default is Synthwave (Light DOM attribute actually maps to Synthwave in CSS root)
    // Dark DOM attribute maps to "Terminal Green"
    
    if (savedTheme === 'terminal') {
        document.body.setAttribute('data-theme', 'dark');
        icon.className = 'fas fa-gamepad'; 
    } else {
        document.body.removeAttribute('data-theme');
        icon.className = 'fas fa-tv';
    }

    themeToggle.addEventListener('click', () => {
        const isTerminal = document.body.getAttribute('data-theme') === 'dark';
        if (isTerminal) {
            document.body.removeAttribute('data-theme'); // Switch to Synthwave
            localStorage.setItem('theme', 'synthwave');
            icon.className = 'fas fa-tv';
        } else {
            document.body.setAttribute('data-theme', 'dark'); // Switch to Terminal Green
            localStorage.setItem('theme', 'terminal');
            icon.className = 'fas fa-gamepad';
        }
    });
}

// --- NAVIGATION LOGIC ---
function setupNavigation() {
    const burger = document.getElementById('burger');
    const nav = document.getElementById('navLinks');
    
    if(burger) {
        burger.addEventListener('click', () => {
            nav.classList.toggle('nav-active');
            burger.classList.toggle('toggle');
        });
    }

    navLinks.forEach(link => {
        link.addEventListener('click', (e) => {
            e.preventDefault();
            const target = link.getAttribute('data-target');
            window.navigateTo(target);
            nav.classList.remove('nav-active'); // Close mobile menu
        });
    });
}

// --- AUTHENTICATION ---
function checkAuth() {
    const token = localStorage.getItem('token');
    const name = localStorage.getItem('userName');
    const roles = JSON.parse(localStorage.getItem('userRoles') || '[]');

    if (token && name) {
        currentUser = { name, roles, token };
        authToken = token;
        updateUIForLoggedInUser();
    } else {
        currentUser = null;
        authToken = null;
        updateUIForGuest();
    }
}

function updateUIForLoggedInUser() {
    authButtons.classList.add('hidden');
    userMenu.classList.remove('hidden');
    welcomeMsg.textContent = `PLAYER: ${currentUser.name.split(' ')[0]}`;
    dashboardLink.classList.remove('hidden');
    
    // Role-based dashboard visibility
    if(currentUser.roles.includes('ROLE_CLUB_HEAD') || currentUser.roles.includes('ROLE_ADMIN')) {
        document.getElementById('createEventCard').classList.remove('hidden');
    }
}

function updateUIForGuest() {
    authButtons.classList.remove('hidden');
    userMenu.classList.add('hidden');
    dashboardLink.classList.add('hidden');
    // Don't force navigate here to allow viewing public pages
}

logoutBtn.addEventListener('click', () => {
    localStorage.clear();
    checkAuth();
    showToast('LOGGED OUT');
    window.navigateTo('home');
});

// Modal Logic
const openModal = () => authModal.classList.remove('hidden');
const hideModal = () => authModal.classList.add('hidden');

loginBtn.addEventListener('click', () => {
    loginContainer.classList.remove('hidden');
    registerContainer.classList.add('hidden');
    openModal();
});
registerBtn.addEventListener('click', () => {
    loginContainer.classList.add('hidden');
    registerContainer.classList.remove('hidden');
    openModal();
});
closeModal.addEventListener('click', hideModal);
authModal.addEventListener('click', (e) => {
    if(e.target === authModal) hideModal();
});

switchRegister.addEventListener('click', (e) => { e.preventDefault(); loginContainer.classList.add('hidden'); registerContainer.classList.remove('hidden'); });
switchLogin.addEventListener('click', (e) => { e.preventDefault(); registerContainer.classList.add('hidden'); loginContainer.classList.remove('hidden'); });

// --- API WRAPPER ---
async function fetchApi(endpoint, method = 'GET', body = null) {
    const headers = { 'Content-Type': 'application/json' };
    if (authToken) headers['Authorization'] = `Bearer ${authToken}`;

    const config = { method, headers };
    if (body) config.body = JSON.stringify(body);

    try {
        const response = await fetch(`${API_BASE_URL}${endpoint}`, config);
        if (response.status === 401) {
            showToast("GAME OVER. SESSION EXPIRED.");
            localStorage.clear();
            checkAuth();
            return null;
        }
        const contentType = response.headers.get("content-type");
        if (contentType && contentType.indexOf("application/json") !== -1) {
            const data = await response.json();
            if(!response.ok) throw new Error(data.message || 'Error occurred');
            return data;
        } else {
            const text = await response.text();
            if(!response.ok) throw new Error(text || 'Error occurred');
            return text;
        }
    } catch (error) {
        console.error(error);
        showToast(error.message || 'NETWORK ERROR');
        return null;
    }
}

// --- FORM SUBMISSIONS ---
function setupForms() {
    loginForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const body = Object.fromEntries(new FormData(loginForm).entries());
        const data = await fetchApi('/api/auth/login', 'POST', body);
        if (data && data.token) {
            localStorage.setItem('token', data.token);
            localStorage.setItem('userName', data.name);
            localStorage.setItem('userRoles', JSON.stringify(data.roles));
            checkAuth();
            hideModal();
            showToast(`WELCOME, ${data.name}!`);
        }
    });

    registerForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const body = Object.fromEntries(new FormData(registerForm).entries());
        const res = await fetchApi('/api/auth/register', 'POST', body);
        if (res) {
            showToast('REGISTRATION COMPLETE. LOGIN NOW.');
            switchLogin.click();
        }
    });

    document.getElementById('clubRequestForm').addEventListener('submit', async (e) => {
        e.preventDefault();
        const formData = new FormData(e.target);
        const body = {
            clubName: formData.get('clubName'),
            description: formData.get('description'),
            category: formData.get('category'),
            proposedMentorName: formData.get('proposedMentorName'),
            memberRegistrationNos: formData.get('memberRegistrationNos').split(',').map(s => s.trim())
        };
        if(body.memberRegistrationNos.length < 5) return showToast('NEED 5+ PARTY MEMBERS');
        if(await fetchApi('/api/clubs/requests', 'POST', body)) {
            showToast('REQUEST TRANSMITTED!');
            e.target.reset();
        }
    });

    document.getElementById('createEventForm').addEventListener('submit', async (e) => {
        e.preventDefault();
        const body = Object.fromEntries(new FormData(e.target).entries());
        body.clubId = parseInt(body.clubId);
        body.venueId = parseInt(body.venueId);
        if(await fetchApi('/api/events', 'POST', body)) {
            showToast('EVENT PUBLISHED!');
            e.target.reset();
        }
    });
}

// --- DATA LOADING ---
async function loadEvents() {
    const container = document.getElementById('eventsContainer');
    const loader = document.getElementById('eventsLoader');
    container.innerHTML = '';
    loader.classList.remove('hidden');

    const events = await fetchApi('/api/events/public/upcoming');
    loader.classList.add('hidden');

    if (events && Array.isArray(events)) {
        if(events.length === 0) return container.innerHTML = '<p style="color:var(--text-main)">NO QUESTS FOUND.</p>';
        
        events.forEach(event => {
            const card = document.createElement('div');
            card.className = 'card';
            
            const typeClass = event.eventType === 'MAJOR' ? 'tag-major' : 'tag-minor';
            let actionBtn = '';
            
            if (currentUser && currentUser.roles.includes('ROLE_STUDENT')) {
                // IMPORTANT: Note the use of window.applyForEvent here in the onclick string
                actionBtn = `<button class="btn btn-primary btn-block" onclick="window.applyForEvent(${event.id}, this)">ACCEPT QUEST</button>`;
            } else if (!currentUser) {
                actionBtn = `<button class="btn btn-outline btn-block" onclick="document.getElementById('loginBtn').click()">LOGIN TO JOIN</button>`;
            }

            card.innerHTML = `
                <div class="card-header">
                    <span class="tag ${typeClass}">${event.eventType || 'EVENT'}</span>
                    <small>${event.status || 'OPEN'}</small>
                </div>
                <div class="card-body">
                    <h3>${event.title}</h3>
                    <p>${event.description.substring(0, 80)}...</p>
                    <div style="font-size:0.9rem; color:var(--text-main); display:flex; flex-direction:column; gap:5px;">
                        <span><i class="far fa-calendar"></i> ${event.eventDate}</span>
                        <span><i class="far fa-clock"></i> ${event.startTime}</span>
                    </div>
                </div>
                <div class="card-footer">
                    ${actionBtn}
                </div>
            `;
            container.appendChild(card);
        });
    }
}

async function loadGallery() {
    const container = document.getElementById('galleryContainer');
    const loader = document.getElementById('galleryLoader');
    container.innerHTML = '';
    loader.classList.remove('hidden');

    const events = await fetchApi('/api/events/public/gallery');
    loader.classList.add('hidden');

    if (events && Array.isArray(events)) {
        events.forEach(event => {
            const card = document.createElement('div');
            card.className = 'card';
            const placeholderImg = `https://ui-avatars.com/api/?name=${event.title}&background=random&size=400`;

            card.innerHTML = `
                <div style="height: 180px; overflow: hidden; border-bottom: 2px solid var(--text-main);">
                     <img src="${placeholderImg}" style="width:100%; height:100%; object-fit: cover; filter: grayscale(100%);" alt="Event">
                </div>
                <div class="card-body">
                    <h3>${event.title}</h3>
                    <p style="color:var(--text-secondary)">${event.eventDate}</p>
                </div>
            `;
            container.appendChild(card);
        });
    }
}

// --- GLOBAL FUNCTIONS ---

// Attached to window to be accessible from HTML onclick attributes
window.applyForEvent = async (eventId, btn) => {
    if(await fetchApi(`/api/events/${eventId}/apply`, 'POST')) {
        showToast('QUEST ACCEPTED!');
        btn.textContent = 'JOINED';
        btn.disabled = true;
    }
};

window.navigateTo = (targetId) => {
    // 1. Update Navigation Links
    navLinks.forEach(l => l.classList.remove('active'));
    const activeLink = document.querySelector(`[data-target="${targetId}"]`);
    if(activeLink) activeLink.classList.add('active');

    // 2. Hide all sections
    sections.forEach(sec => sec.classList.add('hidden'));

    // 3. Show target section
    const target = document.getElementById(targetId);
    if (target) {
        target.classList.remove('hidden');
    } else {
        console.error(`Section with ID '${targetId}' not found`);
    }

    // 4. Load data if specific pages are opened
    if(targetId === 'events') loadEvents();
    if(targetId === 'gallery') loadGallery();
};

function showToast(msg) {
    // Robust check for the retro toast structure
    const toastMsg = toast.querySelector('.toast-msg');
    
    if (toastMsg) {
        toastMsg.textContent = msg;
    } else {
        toast.textContent = msg;
    }

    toast.classList.remove('hidden');
    setTimeout(() => toast.classList.add('hidden'), 3000);
}