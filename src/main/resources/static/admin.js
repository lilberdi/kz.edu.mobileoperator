// Zolik Admin Panel JavaScript.
// Handles navigation between sections, i18n, phone masking and communication with the Spring Boot REST API using Fetch.

// In-memory caches to support simple client-side filtering.
let tariffsCache = [];
let customersCache = [];
let subscriptionsCache = [];
let subscriptionsChart = null;
let currentLang = 'en';

// --- Simple front-end i18n dictionary for EN / RU / KK ---
// Only keys used in data-i18n-key / data-i18n-placeholder are defined here.
const translations = {
    en: {
        'brand.name': 'Zolik',
        'brand.subtitle': 'Mobile Operator Admin Panel',
        'nav.settings': 'Settings',
        'nav.dashboard': 'Dashboard',
        'nav.tariffs': 'Tariffs',
        'nav.customers': 'Customers',
        'nav.subscriptions': 'Subscriptions',
        'nav.logout': 'Logout',
        'nav.help': 'Help & Support',
        'sidebar.mainNav': 'Main navigation',
        'sidebar.other': 'Other',
        'user.signedInAs': 'Signed in as',
        'dashboard.title': 'Dashboard Overview',
        'dashboard.subtitle': 'High-level metrics for Zolik mobile operator.',
        'dashboard.lastUpdated': 'Last updated:',
        'dashboard.totalCustomers': 'Total Customers',
        'dashboard.activeTariffs': 'Active Tariffs',
        'dashboard.activeSubscriptions': 'Active Subscriptions',
        'dashboard.monthlyRevenue': 'Monthly Revenue',
        'dashboard.subsGrowthTitle': 'Subscriptions Growth',
        'dashboard.subsGrowthSubtitle': 'Track overall growth of active subscriptions.',
        'dashboard.periodMonthly': 'Monthly',
        'dashboard.periodQuarterly': 'Quarterly',
        'dashboard.periodYearly': 'Yearly',
        'dashboard.recentActivity': 'Recent Activity',
        'dashboard.viewAll': 'View all',
        'dashboard.noActivity': 'No recent events yet.',
        'common.delete': 'Delete',
        'confirm.tariffDelete': 'Delete this tariff?',
        'confirm.customerDelete': 'Delete this customer?',
        'confirm.subscriptionDelete': 'Delete this subscription?',
        'alerts.tariffStatusUpdated': 'Tariff status updated',
        'alerts.tariffDeleted': 'Tariff deleted',
        'alerts.customerStatusUpdated': 'Customer status updated',
        'alerts.customerDeleted': 'Customer deleted',
        'alerts.subscriptionStatusUpdated': 'Subscription status updated',
        'alerts.subscriptionDeleted': 'Subscription deleted',
        'time.justNow': 'just now',
        'time.minutesAgo': '{n} min ago',
        'time.hoursAgo': '{n} h ago',
        'time.daysAgo': '{n} d ago',
        'tariffs.title': 'Tariffs',
        'tariffs.subtitle': 'Create and manage Zolik mobile tariffs.',
        'tariffs.createTitle': 'Create Tariff',
        'tariffs.form.code': 'Tariff code',
        'tariffs.form.name': 'Tariff name',
        'tariffs.form.fee': 'Monthly fee (₸)',
        'tariffs.form.minutes': 'Minutes',
        'tariffs.form.sms': 'SMS',
        'tariffs.form.gb': 'GB',
        'tariffs.form.saveBtn': 'Save tariff',
        'tariffs.listTitle': 'Tariff list',
        'tariffs.filter.all': 'All statuses',
        'tariffs.filter.active': 'Active',
        'tariffs.filter.archived': 'Archived',
        'tariffs.loadBtn': 'Load tariffs',
        'tariffs.table.code': 'Code',
        'tariffs.table.name': 'Name',
        'tariffs.table.fee': 'Monthly fee',
        'tariffs.table.minutes': 'Minutes',
        'tariffs.table.sms': 'SMS',
        'tariffs.table.gb': 'GB',
        'tariffs.table.status': 'Status',
        'tariffs.table.actions': 'Actions',
        'tariffs.actions.archive': 'Archive',
        'tariffs.actions.activate': 'Activate',
        'tariffs.table.actions': 'Actions',
        'customers.title': 'Customers',
        'customers.createTitle': 'Create Customer',
        'customers.form.phone': 'Phone number',
        'customers.form.createBtn': 'Create customer',
        'customers.listTitle': 'Customers list',
        'customers.loadBtn': 'Load customers',
        'customers.table.id': 'ID',
        'customers.table.name': 'Full name',
        'customers.table.phone': 'Phone',
        'customers.table.status': 'Status',
        'customers.table.actions': 'Actions',
        'customers.actions.block': 'Block',
        'customers.actions.unblock': 'Unblock',
        'customers.table.actions': 'Actions',
        'subscriptions.title': 'Subscriptions',
        'subscriptions.connectTitle': 'Connect Tariff to Customer',
        'subscriptions.form.customer': 'Customer',
        'subscriptions.form.tariff': 'Tariff',
        'subscriptions.form.createBtn': 'Create subscription',
        'subscriptions.listTitle': 'Subscriptions list',
        'subscriptions.table.id': 'ID',
        'subscriptions.table.customer': 'Customer',
        'subscriptions.table.tariff': 'Tariff',
        'subscriptions.table.startDate': 'Start date',
        'subscriptions.table.status': 'Status',
        'subscriptions.table.balance': 'Balance',
        'subscriptions.table.actions': 'Actions',
        'subscriptions.actions.terminate': 'Terminate',
        'subscriptions.table.actions': 'Actions'
    },
    ru: {
        'brand.name': 'Zolik',
        'brand.subtitle': 'Панель администратора мобильного оператора',
        'nav.settings': 'Настройки',
        'nav.dashboard': 'Дашборд',
        'nav.tariffs': 'Тарифы',
        'nav.customers': 'Клиенты',
        'nav.subscriptions': 'Подписки',
        'nav.logout': 'Выход',
        'nav.help': 'Помощь и поддержка',
        'sidebar.mainNav': 'Основное меню',
        'sidebar.other': 'Прочее',
        'user.signedInAs': 'Вы вошли как',
        'dashboard.title': 'Обзор показателей',
        'dashboard.subtitle': 'Ключевые метрики мобильного оператора Zolik.',
        'dashboard.lastUpdated': 'Обновлено:',
        'dashboard.totalCustomers': 'Всего клиентов',
        'dashboard.activeTariffs': 'Активные тарифы',
        'dashboard.activeSubscriptions': 'Активные подписки',
        'dashboard.monthlyRevenue': 'Месячная выручка',
        'dashboard.subsGrowthTitle': 'Рост подписок',
        'dashboard.subsGrowthSubtitle': 'Динамика количества активных подписок.',
        'dashboard.periodMonthly': 'По месяцам',
        'dashboard.periodQuarterly': 'По кварталам',
        'dashboard.periodYearly': 'По годам',
        'dashboard.recentActivity': 'Недавняя активность',
        'dashboard.viewAll': 'Показать всё',
        'dashboard.noActivity': 'Пока нет событий.',
        'common.delete': 'Удалить',
        'confirm.tariffDelete': 'Удалить этот тариф?',
        'confirm.customerDelete': 'Удалить этого клиента?',
        'confirm.subscriptionDelete': 'Удалить эту подписку?',
        'alerts.tariffStatusUpdated': 'Статус тарифа изменён',
        'alerts.tariffDeleted': 'Тариф удалён',
        'alerts.customerStatusUpdated': 'Статус клиента изменён',
        'alerts.customerDeleted': 'Клиент удалён',
        'alerts.subscriptionStatusUpdated': 'Статус подписки изменён',
        'alerts.subscriptionDeleted': 'Подписка удалена',
        'time.justNow': 'только что',
        'time.minutesAgo': '{n} мин назад',
        'time.hoursAgo': '{n} ч назад',
        'time.daysAgo': '{n} дн назад',
        'tariffs.title': 'Тарифы',
        'tariffs.subtitle': 'Создание и управление тарифами Zolik.',
        'tariffs.createTitle': 'Создать тариф',
        'tariffs.form.code': 'Код тарифа',
        'tariffs.form.name': 'Название тарифа',
        'tariffs.form.fee': 'Абонентская плата (₸)',
        'tariffs.form.minutes': 'Минуты',
        'tariffs.form.sms': 'SMS',
        'tariffs.form.gb': 'ГБ',
        'tariffs.form.saveBtn': 'Сохранить тариф',
        'tariffs.listTitle': 'Список тарифов',
        'tariffs.filter.all': 'Все статусы',
        'tariffs.filter.active': 'Активные',
        'tariffs.filter.archived': 'Архивные',
        'tariffs.loadBtn': 'Загрузить тарифы',
        'tariffs.table.code': 'Код',
        'tariffs.table.name': 'Название',
        'tariffs.table.fee': 'Абонплата',
        'tariffs.table.minutes': 'Минуты',
        'tariffs.table.sms': 'SMS',
        'tariffs.table.gb': 'ГБ',
        'tariffs.table.status': 'Статус',
        'tariffs.table.actions': 'Действия',
        'tariffs.actions.archive': 'В архив',
        'tariffs.actions.activate': 'Активировать',
        'tariffs.table.actions': 'Действия',
        'customers.title': 'Клиенты',
        'customers.createTitle': 'Создать клиента',
        'customers.form.phone': 'Номер телефона',
        'customers.form.createBtn': 'Создать клиента',
        'customers.listTitle': 'Список клиентов',
        'customers.loadBtn': 'Загрузить клиентов',
        'customers.table.id': 'ID',
        'customers.table.name': 'ФИО',
        'customers.table.phone': 'Телефон',
        'customers.table.status': 'Статус',
        'customers.table.actions': 'Действия',
        'customers.actions.block': 'Заблокировать',
        'customers.actions.unblock': 'Разблокировать',
        'customers.table.actions': 'Действия',
        'subscriptions.title': 'Подписки',
        'subscriptions.connectTitle': 'Подключить тариф клиенту',
        'subscriptions.form.customer': 'Клиент',
        'subscriptions.form.tariff': 'Тариф',
        'subscriptions.form.createBtn': 'Создать подписку',
        'subscriptions.listTitle': 'Список подписок',
        'subscriptions.table.id': 'ID',
        'subscriptions.table.customer': 'Клиент',
        'subscriptions.table.tariff': 'Тариф',
        'subscriptions.table.startDate': 'Дата начала',
        'subscriptions.table.status': 'Статус',
        'subscriptions.table.balance': 'Баланс',
        'subscriptions.table.actions': 'Действия',
        'subscriptions.actions.terminate': 'Расторгнуть',
    },
    kk: {
        'brand.name': 'Zolik',
        'brand.subtitle': 'Ұялы оператор әкімші панелі',
        'nav.settings': 'Баптаулар',
        'nav.dashboard': 'Дашборд',
        'nav.tariffs': 'Тарифтер',
        'nav.customers': 'Клиенттер',
        'nav.subscriptions': 'Жазылымдар',
        'nav.logout': 'Шығу',
        'nav.help': 'Көмек және қолдау',
        'sidebar.mainNav': 'Негізгі мәзір',
        'sidebar.other': 'Басқа',
        'user.signedInAs': 'Пайдаланушы',
        'dashboard.title': 'Жалпы шолу',
        'dashboard.subtitle': 'Zolik операторының негізгі көрсеткіштері.',
        'dashboard.lastUpdated': 'Жаңартылған:',
        'dashboard.totalCustomers': 'Клиенттер саны',
        'dashboard.activeTariffs': 'Белсенді тарифтер',
        'dashboard.activeSubscriptions': 'Белсенді жазылымдар',
        'dashboard.monthlyRevenue': 'Айлық табыс',
        'dashboard.subsGrowthTitle': 'Жазылымдар өсімі',
        'dashboard.subsGrowthSubtitle': 'Белсенді жазылымдар динамикасы.',
        'dashboard.periodMonthly': 'Айлар',
        'dashboard.periodQuarterly': 'Тоқсандар',
        'dashboard.periodYearly': 'Жылдар',
        'dashboard.recentActivity': 'Соңғы белсенділік',
        'dashboard.viewAll': 'Барлығын көру',
        'dashboard.noActivity': 'Әзірге оқиғалар жоқ.',
        'common.delete': 'Жою',
        'confirm.tariffDelete': 'Бұл тарифті жою?',
        'confirm.customerDelete': 'Бұл клиентті жою?',
        'confirm.subscriptionDelete': 'Бұл жазылымды жою?',
        'alerts.tariffStatusUpdated': 'Тариф статусы өзгертілді',
        'alerts.tariffDeleted': 'Тариф жойылды',
        'alerts.customerStatusUpdated': 'Клиент статусы өзгертілді',
        'alerts.customerDeleted': 'Клиент жойылды',
        'alerts.subscriptionStatusUpdated': 'Жазылым статусы өзгертілді',
        'alerts.subscriptionDeleted': 'Жазылым жойылды',
        'time.justNow': 'қазір ғана',
        'time.minutesAgo': '{n} мин бұрын',
        'time.hoursAgo': '{n} сағ бұрын',
        'time.daysAgo': '{n} күн бұрын',
        'tariffs.title': 'Тарифтер',
        'tariffs.subtitle': 'Zolik тарифтерін құру және басқару.',
        'tariffs.createTitle': 'Тариф құру',
        'tariffs.form.code': 'Тариф коды',
        'tariffs.form.name': 'Тариф атауы',
        'tariffs.form.fee': 'Айлық төлем (₸)',
        'tariffs.form.minutes': 'Минуттар',
        'tariffs.form.sms': 'SMS',
        'tariffs.form.gb': 'ГБ',
        'tariffs.form.saveBtn': 'Тарифті сақтау',
        'tariffs.listTitle': 'Тарифтер тізімі',
        'tariffs.filter.all': 'Барлық статустар',
        'tariffs.filter.active': 'Белсенді',
        'tariffs.filter.archived': 'Архив',
        'tariffs.loadBtn': 'Тарифтерді жүктеу',
        'tariffs.table.code': 'Код',
        'tariffs.table.name': 'Атауы',
        'tariffs.table.fee': 'Айлық төлем',
        'tariffs.table.minutes': 'Минут',
        'tariffs.table.sms': 'SMS',
        'tariffs.table.gb': 'ГБ',
        'tariffs.table.status': 'Статус',
        'tariffs.table.actions': 'Әрекеттер',
        'tariffs.actions.archive': 'Архивке',
        'tariffs.actions.activate': 'Белсенді ету',
        'customers.title': 'Клиенттер',
        'customers.createTitle': 'Клиент құру',
        'customers.form.phone': 'Телефон нөмірі',
        'customers.form.createBtn': 'Клиентті құру',
        'customers.listTitle': 'Клиенттер тізімі',
        'customers.loadBtn': 'Клиенттерді жүктеу',
        'customers.table.id': 'ID',
        'customers.table.name': 'Аты-жөні',
        'customers.table.phone': 'Телефон',
        'customers.table.status': 'Статус',
        'customers.table.actions': 'Әрекеттер',
        'customers.actions.block': 'Блоктау',
        'customers.actions.unblock': 'Блокты ашу',
        'subscriptions.title': 'Жазылымдар',
        'subscriptions.connectTitle': 'Тарифті клиентке қосу',
        'subscriptions.form.customer': 'Клиент',
        'subscriptions.form.tariff': 'Тариф',
        'subscriptions.form.createBtn': 'Жазылым құру',
        'subscriptions.listTitle': 'Жазылымдар тізімі',
        'subscriptions.table.id': 'ID',
        'subscriptions.table.customer': 'Клиент',
        'subscriptions.table.tariff': 'Тариф',
        'subscriptions.table.startDate': 'Басталу күні',
        'subscriptions.table.status': 'Статус',
        'subscriptions.table.balance': 'Баланс',
        'subscriptions.table.actions': 'Әрекеттер',
        'subscriptions.actions.terminate': 'Тоқтату'
    }
};

/**
 * Возвращает словарь переводов для указанного языка.
 */
function getDict(lang) {
    return translations[lang] || translations.en;
}

/**
 * Получить перевод по ключу для текущего языка.
 */
function t(key, fallback) {
    const dict = getDict(currentLang);
    return dict[key] || fallback || key;
}

/**
 * Applies translations for the given language to all elements
 * that have data-i18n-key / data-i18n-placeholder attributes.
 */
function applyTranslations(lang) {
    currentLang = lang;
    const dict = getDict(lang);

    document.documentElement.lang = lang;

    document.querySelectorAll('[data-i18n-key]').forEach(el => {
        const key = el.getAttribute('data-i18n-key');
        if (dict[key]) {
            el.textContent = dict[key];
        }
    });

    document.querySelectorAll('[data-i18n-placeholder]').forEach(el => {
        const key = el.getAttribute('data-i18n-placeholder');
        if (dict[key]) {
            el.setAttribute('placeholder', dict[key]);
        }
    });

    // Highlight active language button
    document.querySelectorAll('.lang-switch').forEach(btn => {
        const btnLang = btn.getAttribute('data-lang');
        if (btnLang === lang) {
            btn.classList.add('text-indigo-600', 'font-semibold');
        } else {
            btn.classList.remove('text-indigo-600', 'font-semibold');
        }
    });
}

/**
 * Sets current language (saved to localStorage) and reapplies translations.
 */
function setLanguage(lang) {
    localStorage.setItem('zolik_lang', lang);
    applyTranslations(lang);
}

/**
 * Initializes language switcher buttons in the navbar.
 */
function setupLanguageSwitcher() {
    const defaultLang = localStorage.getItem('zolik_lang') || 'en';
    applyTranslations(defaultLang);

    document.querySelectorAll('.lang-switch').forEach(btn => {
        btn.addEventListener('click', () => {
            const lang = btn.getAttribute('data-lang');
            setLanguage(lang);
        });
    });
}

/**
 * Renders a Tailwind-styled alert in the alert container.
 * Used across the admin panel to show success/error messages.
 */
function showAlert(type, message) {
    const container = document.getElementById('alert-container');
    if (!container) return;

    const baseClasses =
        'flex items-start space-x-2 rounded-md border px-3 py-2 text-xs shadow-sm bg-white';
    const successClasses =
        'border-emerald-200 text-emerald-800';
    const errorClasses =
        'border-rose-200 text-rose-800';

    const wrapper = document.createElement('div');
    wrapper.className = `${baseClasses} ${type === 'success' ? successClasses : errorClasses}`;

    const icon = document.createElement('span');
    icon.className =
        'mt-0.5 inline-flex h-4 w-4 items-center justify-center rounded-full bg-emerald-50';
    icon.innerHTML =
        type === 'success'
            ? '<svg class="h-3 w-3 text-emerald-600" viewBox="0 0 20 20" fill="currentColor"><path fill-rule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-7.01 7.01a1 1 0 01-1.414 0l-3.01-3.01a1 1 0 011.414-1.414L9 11.586l6.293-6.293a1 1 0 011.414 0z" clip-rule="evenodd" /></svg>'
            : '<svg class="h-3 w-3 text-rose-600" viewBox="0 0 20 20" fill="currentColor"><path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm-1-5a1 1 0 112 0 1 1 0 01-2 0zm0-6a1 1 0 012 0v4a1 1 0 11-2 0V7z" clip-rule="evenodd" /></svg>';

    const text = document.createElement('div');
    text.textContent = message;

    const closeBtn = document.createElement('button');
    closeBtn.type = 'button';
    closeBtn.className = 'ml-auto text-gray-400 hover:text-gray-600';
    closeBtn.innerHTML =
        '<svg class="h-3 w-3" viewBox="0 0 20 20" fill="currentColor"><path fill-rule="evenodd" d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z" clip-rule="evenodd" /></svg>';
    closeBtn.addEventListener('click', () => wrapper.remove());

    wrapper.appendChild(icon);
    wrapper.appendChild(text);
    wrapper.appendChild(closeBtn);
    container.appendChild(wrapper);

    // Auto-remove after a few seconds.
    setTimeout(() => wrapper.remove(), 6000);
}

/**
 * Helper to parse error responses from backend ErrorResponse.
 */
async function parseError(response) {
    try {
        const data = await response.json();
        let msg = data.message || 'Request failed';
        if (Array.isArray(data.details) && data.details.length > 0) {
            msg += ': ' + data.details.join(', ');
        }
        return msg;
    } catch (e) {
        return `Request failed with status ${response.status}`;
    }
}

/**
 * Switches visible content section when user clicks sidebar navigation.
 */
function setupSidebarNavigation() {
    const links = document.querySelectorAll('.sidebar-link[data-section]');
    const sections = document.querySelectorAll('section[id$="Section"]');

    links.forEach(link => {
        link.addEventListener('click', () => {
            const targetId = link.getAttribute('data-section');

            // Update active state on links
            links.forEach(l => {
                l.classList.remove('bg-indigo-50', 'border-indigo-500', 'text-indigo-700');
                l.classList.remove('inline-flex', 'bg-indigo-50', 'text-indigo-700');
            });

            if (link.closest('aside.md\\:flex')) {
                // desktop sidebar link
                link.classList.add('bg-indigo-50', 'border-indigo-500', 'text-indigo-700');
            } else {
                // mobile pill link
                link.classList.add('bg-indigo-50', 'text-indigo-700');
            }

            // Show selected section
            sections.forEach(section => {
                section.classList.toggle('hidden', section.id !== targetId);
            });
        });
    });
}

/**
 * Loads dashboard summary from /api/dashboard/summary and fills the cards.
 */
async function loadDashboardSummary() {
    try {
        const response = await fetch('/api/dashboard/summary');
        if (!response.ok) {
            const errorMsg = await parseError(response);
            showAlert('error', errorMsg);
            return;
        }
        const data = await response.json();

        document.getElementById('totalCustomersValue').textContent = data.totalCustomers ?? '0';
        document.getElementById('activeTariffsValue').textContent = data.activeTariffs ?? '0';
        document.getElementById('activeSubscriptionsValue').textContent = data.activeSubscriptions ?? '0';
        document.getElementById('monthlyRevenueValue').textContent =
            data.monthlyRevenue != null ? `₸${data.monthlyRevenue}` : '₸0';

        const lastUpdatedEl = document.getElementById('dashboardLastUpdated');
        const now = new Date();
        lastUpdatedEl.textContent = now.toLocaleString();
    } catch (error) {
        showAlert('error', 'Failed to load dashboard summary: ' + error.message);
    }
}

/**
 * Loads recent activity items from backend and renders the card.
 */
async function loadRecentActivity() {
    try {
        const response = await fetch('/api/dashboard/recent-activity?limit=5');
        if (!response.ok) {
            const errorMsg = await parseError(response);
            showAlert('error', errorMsg);
            return;
        }
        const items = await response.json();
        renderRecentActivity(items);
    } catch (error) {
        showAlert('error', 'Failed to load recent activity: ' + error.message);
    }
}

/**
 * Renders the Recent Activity list using data from backend.
 */
function renderRecentActivity(items) {
    const container = document.getElementById('recentActivityList');
    if (!container) return;
    container.innerHTML = '';

    if (!items || items.length === 0) {
        const li = document.createElement('li');
        li.className = 'text-xs text-gray-400';
        li.textContent = t('dashboard.noActivity', 'No recent events yet.');
        container.appendChild(li);
        return;
    }

    items.forEach(item => {
        const li = document.createElement('li');
        li.className = 'flex items-start space-x-3';

        let icon = 'user';
        let bgClass = 'bg-gray-100';
        let iconColor = 'text-gray-500';
        if (item.entityType === 'CUSTOMER') {
            icon = 'user-plus';
            bgClass = 'bg-indigo-50';
            iconColor = 'text-indigo-600';
        } else if (item.entityType === 'TARIFF') {
            icon = 'layers';
            bgClass = 'bg-amber-50';
            iconColor = 'text-amber-600';
        } else if (item.entityType === 'SUBSCRIPTION') {
            icon = 'repeat';
            bgClass = 'bg-emerald-50';
            iconColor = 'text-emerald-600';
        }

        const iconSpan = document.createElement('span');
        iconSpan.className = `mt-1 inline-flex h-7 w-7 items-center justify-center rounded-full ${bgClass}`;
        iconSpan.innerHTML = `<i data-feather="${icon}" class="h-3 w-3 ${iconColor}"></i>`;

        const textWrapper = document.createElement('div');
        const title = document.createElement('p');
        title.className = 'font-medium text-gray-900';
        title.textContent = item.message;
        const time = document.createElement('p');
        time.className = 'text-xs text-gray-500';
        time.textContent = formatTimeAgo(new Date(item.timestamp));

        textWrapper.appendChild(title);
        textWrapper.appendChild(time);

        li.appendChild(iconSpan);
        li.appendChild(textWrapper);
        container.appendChild(li);
    });

    if (window.feather) {
        window.feather.replace();
    }
}
/**
 * Loads subscription stats from /api/dashboard/subscriptions-stats and draws Chart.js line chart.
 * @param {'monthly'|'quarterly'|'yearly'} period
 */
async function loadSubscriptionsChart(period = 'monthly') {
    const canvas = document.getElementById('subscriptionsChart');
    if (!canvas || !window.Chart) return;

    try {
        const response = await fetch(`/api/dashboard/subscriptions-stats?period=${encodeURIComponent(period)}`);
        if (!response.ok) {
            const errorMsg = await parseError(response);
            showAlert('error', errorMsg);
            return;
        }
        const stats = await response.json();
        const labels = stats.map(s => s.month);
        const activeData = stats.map(s => s.activeCount);

        const ctx = canvas.getContext('2d');
        if (subscriptionsChart) {
            subscriptionsChart.destroy();
        }
        subscriptionsChart = new Chart(ctx, {
            type: 'line',
            data: {
                labels,
                datasets: [
                    {
                        label: 'Active subscriptions',
                        data: activeData,
                        borderColor: '#4f46e5',
                        backgroundColor: 'rgba(79, 70, 229, 0.1)',
                        tension: 0.3,
                        fill: true,
                        pointRadius: 2
                    }
                ]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                scales: {
                    x: {
                        ticks: { font: { size: 10 } }
                    },
                    y: {
                        beginAtZero: true,
                        ticks: { font: { size: 10 } }
                    }
                },
                plugins: {
                    legend: {
                        labels: { font: { size: 10 } }
                    }
                }
            }
        });
    } catch (error) {
        showAlert('error', 'Failed to load subscriptions statistics: ' + error.message);
    }
}

/**
 * Loads tariffs from backend and updates global cache + table.
 */
async function loadTariffs() {
    try {
        const response = await fetch('/api/tariffs');
        if (!response.ok) {
            const errorMsg = await parseError(response);
            showAlert('error', errorMsg);
            return;
        }
        tariffsCache = await response.json();
        renderTariffsTable();
        showAlert('success', 'Tariffs loaded successfully');
    } catch (error) {
        showAlert('error', 'Failed to load tariffs: ' + error.message);
    }
}

/**
 * Renders tariffs table using cache and current filters.
 */
function renderTariffsTable() {
    const tbody = document.getElementById('tariffsTableBody');
    if (!tbody) return;

    const search = (document.getElementById('tariffSearchInput')?.value || '').toLowerCase();
    const statusFilter = document.getElementById('tariffStatusFilter')?.value || '';

    tbody.innerHTML = '';
    tariffsCache
        .filter(t => {
            const matchesSearch =
                !search ||
                t.code.toLowerCase().includes(search) ||
                t.name.toLowerCase().includes(search);
            const matchesStatus = !statusFilter || t.status === statusFilter;
            return matchesSearch && matchesStatus;
        })
        .forEach(tariff => {
            const tr = document.createElement('tr');
            tr.className = 'hover:bg-gray-50';

            const statusColor =
                tariff.status === 'ACTIVE'
                    ? 'bg-emerald-50 text-emerald-700'
                    : 'bg-gray-100 text-gray-600';

            const toggleLabel = tariff.status === 'ACTIVE'
                ? t('tariffs.actions.archive', 'Archive')
                : t('tariffs.actions.activate', 'Activate');
            const toggleStatus = tariff.status === 'ACTIVE' ? 'ARCHIVED' : 'ACTIVE';

            tr.innerHTML = `
                <td class="px-3 py-2 whitespace-nowrap text-xs font-medium text-gray-900">${tariff.code}</td>
                <td class="px-3 py-2 whitespace-nowrap text-xs text-gray-700">${tariff.name}</td>
                <td class="px-3 py-2 whitespace-nowrap text-xs text-gray-700">₸${tariff.monthlyFee}</td>
                <td class="px-3 py-2 whitespace-nowrap text-xs text-gray-500">${tariff.includedMinutes ?? ''}</td>
                <td class="px-3 py-2 whitespace-nowrap text-xs text-gray-500">${tariff.includedSms ?? ''}</td>
                <td class="px-3 py-2 whitespace-nowrap text-xs text-gray-500">${tariff.includedGb ?? ''}</td>
                <td class="px-3 py-2 whitespace-nowrap text-xs">
                    <span class="inline-flex items-center rounded-full px-2 py-0.5 text-[11px] font-medium ${statusColor}">
                        ${tariff.status}
                    </span>
                </td>
                <td class="px-3 py-2 whitespace-nowrap text-xs text-right space-x-1">
                    <button class="inline-flex items-center rounded-md border border-gray-200 bg-white px-2 py-0.5 text-[11px] text-gray-700 hover:bg-gray-50"
                            data-action="toggle-status">
                        ${toggleLabel}
                    </button>
                    <button class="inline-flex items-center rounded-md border border-rose-200 bg-white px-2 py-0.5 text-[11px] text-rose-600 hover:bg-rose-50"
                            data-action="delete">
                        ${t('common.delete', 'Delete')}
                    </button>
                </td>
            `;

            const toggleBtn = tr.querySelector('button[data-action="toggle-status"]');
            const deleteBtn = tr.querySelector('button[data-action="delete"]');
            toggleBtn.addEventListener('click', () => changeTariffStatus(tariff.id, toggleStatus));
            deleteBtn.addEventListener('click', () => deleteTariff(tariff.id));

            tbody.appendChild(tr);
        });
}

/**
 * Creates a new tariff using form inputs and POST /api/tariffs.
 */
async function createTariff() {
    const code = document.getElementById('tariffCode').value.trim();
    const name = document.getElementById('tariffName').value.trim();
    const monthlyFee = document.getElementById('tariffMonthlyFee').value;
    const includedMinutes = document.getElementById('tariffMinutes').value;
    const includedSms = document.getElementById('tariffSms').value;
    const includedGb = document.getElementById('tariffGb').value;

    const payload = {
        code,
        name,
        monthlyFee: monthlyFee || null,
        includedMinutes: includedMinutes ? Number(includedMinutes) : null,
        includedSms: includedSms ? Number(includedSms) : null,
        includedGb: includedGb ? Number(includedGb) : null
    };

    try {
        const response = await fetch('/api/tariffs', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        if (!response.ok) {
            const errorMsg = await parseError(response);
            showAlert('error', errorMsg);
            return;
        }

        document.getElementById('tariffForm').reset();
        showAlert('success', 'Tariff created successfully');
        await loadTariffs();
    } catch (error) {
        showAlert('error', 'Failed to create tariff: ' + error.message);
    }
}

/**
 * Loads customers from backend and updates cache + table.
 */
async function loadCustomers() {
    try {
        const response = await fetch('/api/customers');
        if (!response.ok) {
            const errorMsg = await parseError(response);
            showAlert('error', errorMsg);
            return;
        }
        customersCache = await response.json();
        renderCustomersTable();
        populateSubscriptionDropdowns();
        showAlert('success', 'Customers loaded successfully');
    } catch (error) {
        showAlert('error', 'Failed to load customers: ' + error.message);
    }
}

/**
 * Renders customers table using cache and simple text search.
 */
function renderCustomersTable() {
    const tbody = document.getElementById('customersTableBody');
    if (!tbody) return;

    const search = (document.getElementById('customerSearchInput')?.value || '').toLowerCase();

    tbody.innerHTML = '';
    customersCache
        .filter(c => {
            const fullName = c.fullName?.toLowerCase() || '';
            const phone = c.phoneNumber?.toLowerCase() || '';
            return !search || fullName.includes(search) || phone.includes(search);
        })
        .forEach(customer => {
            const statusColor =
                customer.status === 'ACTIVE'
                    ? 'bg-emerald-50 text-emerald-700'
                    : 'bg-rose-50 text-rose-700';

            const tr = document.createElement('tr');
            tr.className = 'hover:bg-gray-50';
            const toggleLabel = customer.status === 'ACTIVE'
                ? t('customers.actions.block', 'Block')
                : t('customers.actions.unblock', 'Unblock');
            const toggleStatus = customer.status === 'ACTIVE' ? 'BLOCKED' : 'ACTIVE';

            tr.innerHTML = `
                <td class="px-3 py-2 whitespace-nowrap text-xs font-medium text-gray-900">${customer.id}</td>
                <td class="px-3 py-2 whitespace-nowrap text-xs text-gray-700">${customer.fullName}</td>
                <td class="px-3 py-2 whitespace-nowrap text-xs text-gray-700">${formatPhone(
                    customer.phoneNumber
                )}</td>
                <td class="px-3 py-2 whitespace-nowrap text-xs">
                    <span class="inline-flex items-center rounded-full px-2 py-0.5 text-[11px] font-medium ${statusColor}">
                        ${customer.status}
                    </span>
                </td>
                <td class="px-3 py-2 whitespace-nowrap text-xs text-right space-x-1">
                    <button class="inline-flex items-center rounded-md border border-gray-200 bg-white px-2 py-0.5 text-[11px] text-gray-700 hover:bg-gray-50"
                            data-action="toggle-status">
                        ${toggleLabel}
                    </button>
                    <button class="inline-flex items-center rounded-md border border-rose-200 bg-white px-2 py-0.5 text-[11px] text-rose-600 hover:bg-rose-50"
                            data-action="delete">
                        ${t('common.delete', 'Delete')}
                    </button>
                </td>
            `;

            const toggleBtn = tr.querySelector('button[data-action="toggle-status"]');
            const deleteBtn = tr.querySelector('button[data-action="delete"]');
            toggleBtn.addEventListener('click', () => changeCustomerStatus(customer.id, toggleStatus));
            deleteBtn.addEventListener('click', () => deleteCustomer(customer.id));

            tbody.appendChild(tr);
        });
}

/**
 * Creates a new customer using POST /api/customers.
 */
async function createCustomer() {
    const fullName = document.getElementById('customerFullName').value.trim();
    const phoneNumber = document.getElementById('customerPhoneNumber').value.trim();
    const documentNumber = document.getElementById('customerDocumentNumber').value.trim();

    const normalizedPhone = normalizePhone(phoneNumber);
    if (!normalizedPhone) {
        showAlert('error', 'Please enter a valid phone number in format +7 (000) 000-00-00');
        return;
    }

    const payload = { fullName, phoneNumber: normalizedPhone, documentNumber };

    try {
        const response = await fetch('/api/customers', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });
        if (!response.ok) {
            const errorMsg = await parseError(response);
            showAlert('error', errorMsg);
            return;
        }

        document.getElementById('customerForm').reset();
        showAlert('success', 'Customer created successfully');
        await loadCustomers();
    } catch (error) {
        showAlert('error', 'Failed to create customer: ' + error.message);
    }
}

/**
 * Populates dropdowns in the subscription form with current customers and tariffs.
 */
function populateSubscriptionDropdowns() {
    const customerSelect = document.getElementById('subscriptionCustomerSelect');
    const tariffSelect = document.getElementById('subscriptionTariffSelect');
    if (!customerSelect || !tariffSelect) return;

    customerSelect.innerHTML = '';
    tariffSelect.innerHTML = '';

    const defaultOption = (text) =>
        `<option value="">${text}</option>`;

    customerSelect.insertAdjacentHTML('beforeend', defaultOption('Select customer...'));
    customersCache.forEach(c => {
        const label = `${c.fullName} (ID ${c.id})`;
        customerSelect.insertAdjacentHTML(
            'beforeend',
            `<option value="${c.id}">${label}</option>`
        );
    });

    tariffSelect.insertAdjacentHTML('beforeend', defaultOption('Select tariff...'));
    tariffsCache.forEach(t => {
        const label = `${t.name} (${t.code})`;
        tariffSelect.insertAdjacentHTML(
            'beforeend',
            `<option value="${t.id}">${label}</option>`
        );
    });
}

/**
 * Connects a tariff to a customer using POST /api/subscriptions/connect.
 */
async function createSubscription() {
    const customerSelect = document.getElementById('subscriptionCustomerSelect');
    const tariffSelect = document.getElementById('subscriptionTariffSelect');
    const customerId = customerSelect.value;
    const tariffId = tariffSelect.value;

    const payload = {
        customerId: customerId ? Number(customerId) : null,
        tariffId: tariffId ? Number(tariffId) : null
    };

    try {
        const response = await fetch('/api/subscriptions/connect', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });
        if (!response.ok) {
            const errorMsg = await parseError(response);
            showAlert('error', errorMsg);
            return;
        }
        document.getElementById('subscriptionForm').reset();
        showAlert('success', 'Subscription created successfully');
        await loadSubscriptions();
    } catch (error) {
        showAlert('error', 'Failed to create subscription: ' + error.message);
    }
}

/**
 * Loads subscriptions list from backend (GET /api/subscriptions).
 */
async function loadSubscriptions() {
    try {
        const response = await fetch('/api/subscriptions');
        if (!response.ok) {
            const errorMsg = await parseError(response);
            showAlert('error', errorMsg);
            return;
        }
        subscriptionsCache = await response.json();
        renderSubscriptionsTable();
        showAlert('success', 'Subscriptions loaded successfully');
    } catch (error) {
        showAlert('error', 'Failed to load subscriptions: ' + error.message);
    }
}

/**
 * Renders subscriptions table using cache.
 */
function renderSubscriptionsTable() {
    const tbody = document.getElementById('subscriptionsTableBody');
    if (!tbody) return;

    tbody.innerHTML = '';
    subscriptionsCache.forEach(s => {
        const statusColor =
            s.status === 'ACTIVE'
                ? 'bg-emerald-50 text-emerald-700'
                : s.status === 'SUSPENDED'
                    ? 'bg-amber-50 text-amber-700'
                    : 'bg-gray-100 text-gray-600';

        const tr = document.createElement('tr');
        tr.className = 'hover:bg-gray-50';
        const canTerminate = s.status !== 'TERMINATED';

        tr.innerHTML = `
            <td class="px-3 py-2 whitespace-nowrap text-xs font-medium text-gray-900">${s.id}</td>
            <td class="px-3 py-2 whitespace-nowrap text-xs text-gray-700">${s.customerId}</td>
            <td class="px-3 py-2 whitespace-nowrap text-xs text-gray-700">${s.tariffId}</td>
            <td class="px-3 py-2 whitespace-nowrap text-xs text-gray-500">${s.startDate ?? ''}</td>
            <td class="px-3 py-2 whitespace-nowrap text-xs">
                <span class="inline-flex items-center rounded-full px-2 py-0.5 text-[11px] font-medium ${statusColor}">
                    ${s.status}
                </span>
            </td>
            <td class="px-3 py-2 whitespace-nowrap text-xs text-gray-700">₸${s.currentBalance}</td>
            <td class="px-3 py-2 whitespace-nowrap text-xs text-right space-x-1">
                ${canTerminate ? `<button class="inline-flex items-center rounded-md border border-gray-200 bg-white px-2 py-0.5 text-[11px] text-gray-700 hover:bg-gray-50" data-action="terminate">
                    ${t('subscriptions.actions.terminate', 'Terminate')}
                </button>` : ''}
                <button class="inline-flex items-center rounded-md border border-rose-200 bg-white px-2 py-0.5 text-[11px] text-rose-600 hover:bg-rose-50" data-action="delete">
                    ${t('common.delete', 'Delete')}
                </button>
            </td>
        `;

        const terminateBtn = tr.querySelector('button[data-action="terminate"]');
        const deleteBtn = tr.querySelector('button[data-action="delete"]');
        if (terminateBtn) {
            terminateBtn.addEventListener('click', () => changeSubscriptionStatus(s.id, 'TERMINATED'));
        }
        deleteBtn.addEventListener('click', () => deleteSubscription(s.id));
        tbody.appendChild(tr);
    });
}

/**
 * Wires up click handlers for all main actions.
 */
function setupEventHandlers() {
    const createTariffBtn = document.getElementById('createTariffBtn');
    const loadTariffsBtn = document.getElementById('loadTariffsBtn');
    const createCustomerBtn = document.getElementById('createCustomerBtn');
    const loadCustomersBtn = document.getElementById('loadCustomersBtn');
    const connectTariffBtn = document.getElementById('connectTariffBtn');
    const loadSubscriptionsBtn = document.getElementById('loadSubscriptionsBtn');
    const scrollToTariffFormBtn = document.getElementById('scrollToTariffFormBtn');
    const scrollToCustomerFormBtn = document.getElementById('scrollToCustomerFormBtn');

    if (createTariffBtn) createTariffBtn.addEventListener('click', createTariff);
    if (loadTariffsBtn) loadTariffsBtn.addEventListener('click', loadTariffs);
    if (createCustomerBtn) createCustomerBtn.addEventListener('click', createCustomer);
    if (loadCustomersBtn) loadCustomersBtn.addEventListener('click', loadCustomers);
    if (connectTariffBtn) connectTariffBtn.addEventListener('click', createSubscription);
    if (loadSubscriptionsBtn) loadSubscriptionsBtn.addEventListener('click', loadSubscriptions);

    if (scrollToTariffFormBtn) {
        scrollToTariffFormBtn.addEventListener('click', () => {
            document.getElementById('tariffFormCard')?.scrollIntoView({ behavior: 'smooth' });
        });
    }
    if (scrollToCustomerFormBtn) {
        scrollToCustomerFormBtn.addEventListener('click', () => {
            document.getElementById('customerFormCard')?.scrollIntoView({ behavior: 'smooth' });
        });
    }

    const tariffSearchInput = document.getElementById('tariffSearchInput');
    const tariffStatusFilter = document.getElementById('tariffStatusFilter');
    if (tariffSearchInput) tariffSearchInput.addEventListener('input', renderTariffsTable);
    if (tariffStatusFilter) tariffStatusFilter.addEventListener('change', renderTariffsTable);

    const customerSearchInput = document.getElementById('customerSearchInput');
    if (customerSearchInput) customerSearchInput.addEventListener('input', renderCustomersTable);

    // Period switchers for subscriptions chart
    const growthPills = document.querySelectorAll('.growth-pill');
    growthPills.forEach(pill => {
        pill.addEventListener('click', () => {
            const period = pill.getAttribute('data-period') || 'monthly';

            growthPills.forEach(p => {
                p.classList.remove('bg-white', 'shadow-sm', 'text-indigo-600');
                p.classList.add('text-gray-500');
            });
            pill.classList.add('bg-white', 'shadow-sm', 'text-indigo-600');

            loadSubscriptionsChart(period);
        });
    });

    const logoutBtn = document.getElementById('logoutBtn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', logout);
    }
}

/**
 * Запрос на смену статуса тарифа.
 */
async function changeTariffStatus(id, newStatus) {
    try {
        const response = await fetch(`/api/tariffs/${id}/status`, {
            method: 'PATCH',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ status: newStatus })
        });
        if (!response.ok) {
            const errorMsg = await parseError(response);
            showAlert('error', errorMsg);
            return;
        }
        showAlert('success', t('alerts.tariffStatusUpdated', 'Tariff status updated'));
        await loadTariffs();
        await loadRecentActivity();
        await loadDashboardSummary();
    } catch (error) {
        showAlert('error', 'Failed to update tariff status: ' + error.message);
    }
}

/**
 * Удаление тарифа.
 */
async function deleteTariff(id) {
    if (!window.confirm(t('confirm.tariffDelete', 'Delete this tariff?'))) return;
    try {
        const response = await fetch(`/api/tariffs/${id}`, { method: 'DELETE' });
        if (!response.ok && response.status !== 204) {
            const errorMsg = await parseError(response);
            showAlert('error', errorMsg);
            return;
        }
        showAlert('success', t('alerts.tariffDeleted', 'Tariff deleted'));
        await loadTariffs();
        await loadRecentActivity();
        await loadDashboardSummary();
    } catch (error) {
        showAlert('error', 'Failed to delete tariff: ' + error.message);
    }
}

/**
 * Смена статуса клиента.
 */
async function changeCustomerStatus(id, newStatus) {
    try {
        const response = await fetch(`/api/customers/${id}/status`, {
            method: 'PATCH',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ status: newStatus })
        });
        if (!response.ok) {
            const errorMsg = await parseError(response);
            showAlert('error', errorMsg);
            return;
        }
        showAlert('success', t('alerts.customerStatusUpdated', 'Customer status updated'));
        await loadCustomers();
        await loadRecentActivity();
        await loadDashboardSummary();
    } catch (error) {
        showAlert('error', 'Failed to update customer status: ' + error.message);
    }
}

/**
 * Удалить клиента.
 */
async function deleteCustomer(id) {
    if (!window.confirm(t('confirm.customerDelete', 'Delete this customer?'))) return;
    try {
        const response = await fetch(`/api/customers/${id}`, { method: 'DELETE' });
        if (!response.ok && response.status !== 204) {
            const errorMsg = await parseError(response);
            showAlert('error', errorMsg);
            return;
        }
        showAlert('success', t('alerts.customerDeleted', 'Customer deleted'));
        await loadCustomers();
        await loadRecentActivity();
        await loadDashboardSummary();
    } catch (error) {
        showAlert('error', 'Failed to delete customer: ' + error.message);
    }
}

/**
 * Сменить статус подписки.
 */
async function changeSubscriptionStatus(id, newStatus) {
    try {
        const response = await fetch(`/api/subscriptions/${id}/status`, {
            method: 'PATCH',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ status: newStatus })
        });
        if (!response.ok) {
            const errorMsg = await parseError(response);
            showAlert('error', errorMsg);
            return;
        }
        showAlert('success', t('alerts.subscriptionStatusUpdated', 'Subscription status updated'));
        await loadSubscriptions();
        await loadRecentActivity();
        await loadDashboardSummary();
    } catch (error) {
        showAlert('error', 'Failed to update subscription status: ' + error.message);
    }
}

/**
 * Удалить подписку.
 */
async function deleteSubscription(id) {
    if (!window.confirm(t('confirm.subscriptionDelete', 'Delete this subscription?'))) return;
    try {
        const response = await fetch(`/api/subscriptions/${id}`, { method: 'DELETE' });
        if (!response.ok && response.status !== 204) {
            const errorMsg = await parseError(response);
            showAlert('error', errorMsg);
            return;
        }
        showAlert('success', t('alerts.subscriptionDeleted', 'Subscription deleted'));
        await loadSubscriptions();
        await loadRecentActivity();
        await loadDashboardSummary();
    } catch (error) {
        showAlert('error', 'Failed to delete subscription: ' + error.message);
    }
}

/**
 * Normalizes phone to backend format (digits only, 11 digits starting with 7).
 */
function normalizePhone(raw) {
    const digits = (raw || '').replace(/\D/g, '');
    if (digits.length === 11 && digits.startsWith('7')) {
        return '+' + digits;
    }
    if (digits.length === 10) {
        return '+7' + digits;
    }
    return null;
}

/**
 * Formats phone from backend format (+7XXXXXXXXXX) to +7 (000) 000-00-00.
 */
function formatPhone(phone) {
    if (!phone) {
        return '';
    }
    const digits = phone.replace(/\D/g, '');
    const core = digits.length === 11 && digits.startsWith('7') ? digits.slice(1) : digits;
    if (core.length !== 10) {
        return phone;
    }
    const part1 = core.slice(0, 3);
    const part2 = core.slice(3, 6);
    const part3 = core.slice(6, 8);
    const part4 = core.slice(8, 10);
    return `+7 (${part1}) ${part2}-${part3}-${part4}`;
}

/**
 * Sets up masking for phone input in Create Customer form.
 */
function setupPhoneMask() {
    const input = document.getElementById('customerPhoneNumber');
    if (!input) return;

    if (window.Cleave) {
        // Use Cleave.js to help input the required format.
        // The prefix "+7" is fixed, the rest is entered as digits.
        // Example: +7 (777) 000-00-00
        // numericOnly ensures only digits for phone body.
        // eslint-disable-next-line no-new
        new Cleave(input, {
            prefix: '+7',
            delimiters: [' (', ') ', ' ', '-', '-'],
            blocks: [2, 3, 3, 2, 2],
            numericOnly: true
        });
    }
}

/**
 * Loads current authenticated user info from /api/auth/me and updates navbar.
 */
async function loadCurrentUser() {
    try {
        const response = await fetch('/api/auth/me');
        if (!response.ok) {
            return;
        }
        const data = await response.json();
        const nameEl = document.getElementById('currentUserName');
        const roleEl = document.getElementById('currentUserRole');
        if (nameEl) {
            nameEl.textContent = data.username;
        }
        if (roleEl) {
            roleEl.textContent = data.role;
        }
    } catch {
        // ignore
    }
}

/**
 * Performs logout by calling Spring Security /logout endpoint.
 */
async function logout() {
    try {
        await fetch('/logout', {
            method: 'POST',
            headers: { 'X-Requested-With': 'XMLHttpRequest' }
        });
    } finally {
        window.location.href = '/login?logout';
    }
}

/**
 * Formats given date as "x min ago" / "x h ago" etc.
 */
function formatTimeAgo(date) {
    const now = new Date();
    const diffMs = now - date;
    const sec = Math.floor(diffMs / 1000);
    const min = Math.floor(sec / 60);
    const hours = Math.floor(min / 60);
    const days = Math.floor(hours / 24);

    if (sec < 60) return t('time.justNow', 'just now');
    if (min < 60) {
        return t('time.minutesAgo', '{n} min ago').replace('{n}', String(min));
    }
    if (hours < 24) {
        return t('time.hoursAgo', '{n} h ago').replace('{n}', String(hours));
    }
    return t('time.daysAgo', '{n} d ago').replace('{n}', String(days));
}

// Initialize admin panel when DOM is ready.
document.addEventListener('DOMContentLoaded', () => {
    setupSidebarNavigation();
    setupLanguageSwitcher();
    setupEventHandlers();
    setupPhoneMask();

    // Initial loads: dashboard + chart + base lists for selects.
    loadDashboardSummary();
    loadSubscriptionsChart('monthly');
    loadRecentActivity();
    loadTariffs().then(populateSubscriptionDropdowns);
    loadCustomers();
    loadCurrentUser();
});

