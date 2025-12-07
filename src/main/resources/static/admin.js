// Simple admin panel JavaScript for interacting with the REST API.
// Uses Fetch API to call the backend and updates the DOM to show results.

/**
 * Shows a global message (success or error) at the top of the page.
 * @param {'success'|'error'} type
 * @param {string} message
 */
function showMessage(type, message) {
    const alertDiv = document.getElementById('globalMessage');
    if (!alertDiv) {
        return;
    }
    alertDiv.classList.remove('d-none', 'alert-success', 'alert-danger');
    alertDiv.classList.add(type === 'success' ? 'alert-success' : 'alert-danger');
    alertDiv.textContent = message;
}

/**
 * Clears the global message.
 */
function clearMessage() {
    const alertDiv = document.getElementById('globalMessage');
    if (!alertDiv) {
        return;
    }
    alertDiv.classList.add('d-none');
    alertDiv.textContent = '';
}

/**
 * Helper function to parse error responses from the backend.
 * Tries to read JSON body with fields from ErrorResponse.
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
 * Loads tariffs from the backend and renders them in the tariffs table.
 */
async function loadTariffs() {
    clearMessage();
    try {
        const response = await fetch('/api/tariffs');
        if (!response.ok) {
            const errorMsg = await parseError(response);
            showMessage('error', errorMsg);
            return;
        }
        const tariffs = await response.json();
        const tbody = document.querySelector('#tariffsTable tbody');
        tbody.innerHTML = '';
        tariffs.forEach(tariff => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td>${tariff.code}</td>
                <td>${tariff.name}</td>
                <td>${tariff.monthlyFee}</td>
                <td>${tariff.includedMinutes ?? ''}</td>
                <td>${tariff.includedSms ?? ''}</td>
                <td>${tariff.includedGb ?? ''}</td>
                <td>${tariff.status}</td>
            `;
            tbody.appendChild(tr);
        });
        showMessage('success', 'Tariffs loaded successfully');
    } catch (error) {
        showMessage('error', 'Failed to load tariffs: ' + error.message);
    }
}

/**
 * Creates a new tariff using the form values and sends POST /api/tariffs.
 */
async function createTariff() {
    clearMessage();
    const code = document.getElementById('tariffCode').value.trim();
    const name = document.getElementById('tariffName').value.trim();
    const monthlyFee = document.getElementById('tariffMonthlyFee').value;
    const includedMinutes = document.getElementById('tariffMinutes').value;
    const includedSms = document.getElementById('tariffSms').value;
    const includedGb = document.getElementById('tariffGb').value;

    const payload = {
        code: code,
        name: name,
        monthlyFee: monthlyFee || null,
        includedMinutes: includedMinutes ? Number(includedMinutes) : null,
        includedSms: includedSms ? Number(includedSms) : null,
        includedGb: includedGb ? Number(includedGb) : null
    };

    try {
        const response = await fetch('/api/tariffs', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(payload)
        });

        if (!response.ok) {
            const errorMsg = await parseError(response);
            showMessage('error', errorMsg);
            return;
        }

        // Clear form and refresh list
        document.getElementById('tariffForm').reset();
        showMessage('success', 'Tariff created successfully');
        await loadTariffs();
    } catch (error) {
        showMessage('error', 'Failed to create tariff: ' + error.message);
    }
}

/**
 * Loads customers from the backend and renders them in the customers table.
 */
async function loadCustomers() {
    clearMessage();
    try {
        const response = await fetch('/api/customers');
        if (!response.ok) {
            const errorMsg = await parseError(response);
            showMessage('error', errorMsg);
            return;
        }
        const customers = await response.json();
        const tbody = document.querySelector('#customersTable tbody');
        tbody.innerHTML = '';
        customers.forEach(customer => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td>${customer.id}</td>
                <td>${customer.fullName}</td>
                <td>${customer.phoneNumber}</td>
                <td>${customer.status}</td>
            `;
            tbody.appendChild(tr);
        });
        showMessage('success', 'Customers loaded successfully');
    } catch (error) {
        showMessage('error', 'Failed to load customers: ' + error.message);
    }
}

/**
 * Creates a new customer using the form values and sends POST /api/customers.
 */
async function createCustomer() {
    clearMessage();
    const fullName = document.getElementById('customerFullName').value.trim();
    const phoneNumber = document.getElementById('customerPhoneNumber').value.trim();
    const documentNumber = document.getElementById('customerDocumentNumber').value.trim();

    const payload = {
        fullName: fullName,
        phoneNumber: phoneNumber,
        documentNumber: documentNumber
    };

    try {
        const response = await fetch('/api/customers', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(payload)
        });

        if (!response.ok) {
            const errorMsg = await parseError(response);
            showMessage('error', errorMsg);
            return;
        }

        document.getElementById('customerForm').reset();
        showMessage('success', 'Customer created successfully');
        await loadCustomers();
    } catch (error) {
        showMessage('error', 'Failed to create customer: ' + error.message);
    }
}

/**
 * Connects a tariff to a customer using POST /api/subscriptions/connect.
 */
async function connectTariff() {
    clearMessage();
    const customerId = document.getElementById('subscriptionCustomerId').value;
    const tariffId = document.getElementById('subscriptionTariffId').value;

    const payload = {
        customerId: customerId ? Number(customerId) : null,
        tariffId: tariffId ? Number(tariffId) : null
    };

    try {
        const response = await fetch('/api/subscriptions/connect', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(payload)
        });

        if (!response.ok) {
            const errorMsg = await parseError(response);
            showMessage('error', errorMsg);
            return;
        }

        document.getElementById('subscriptionForm').reset();
        showMessage('success', 'Subscription created (tariff connected to customer)');
    } catch (error) {
        showMessage('error', 'Failed to connect tariff: ' + error.message);
    }
}

/**
 * Simple tab switching logic: shows one section and hides others.
 */
function setupTabs() {
    const tabButtons = document.querySelectorAll('#adminTabs button[data-section]');
    const sections = document.querySelectorAll('.tab-section');

    tabButtons.forEach(button => {
        button.addEventListener('click', () => {
            const targetId = button.getAttribute('data-section');

            // Update active tab button
            tabButtons.forEach(b => b.classList.remove('active'));
            button.classList.add('active');

            // Show only the selected section
            sections.forEach(section => {
                if (section.id === targetId) {
                    section.classList.add('active');
                } else {
                    section.classList.remove('active');
                }
            });
        });
    });
}

/**
 * Sets up event listeners for all buttons when the page is loaded.
 */
function setupEventHandlers() {
    const createTariffBtn = document.getElementById('createTariffBtn');
    const loadTariffsBtn = document.getElementById('loadTariffsBtn');
    const createCustomerBtn = document.getElementById('createCustomerBtn');
    const loadCustomersBtn = document.getElementById('loadCustomersBtn');
    const connectTariffBtn = document.getElementById('connectTariffBtn');

    if (createTariffBtn) {
        createTariffBtn.addEventListener('click', createTariff);
    }
    if (loadTariffsBtn) {
        loadTariffsBtn.addEventListener('click', loadTariffs);
    }
    if (createCustomerBtn) {
        createCustomerBtn.addEventListener('click', createCustomer);
    }
    if (loadCustomersBtn) {
        loadCustomersBtn.addEventListener('click', loadCustomers);
    }
    if (connectTariffBtn) {
        connectTariffBtn.addEventListener('click', connectTariff);
    }
}

// Initialize the admin panel once the DOM is ready.
document.addEventListener('DOMContentLoaded', () => {
    setupTabs();
    setupEventHandlers();
});


