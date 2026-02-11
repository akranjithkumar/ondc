/**
 * ONDC Vendor Dashboard — Frontend Application
 * Connects to the Spring Boot backend REST APIs and provides
 * real-time inventory management with seller app sync.
 */
const app = (() => {
    // === Config ===
    const API_BASE = 'http://10.176.65.28:8080';  // Backend server IP
    const VENDOR_ID = 1;  // Default vendor for demo

    // === State ===
    let dashboardData = null;
    let inventoryData = [];
    let sellerApps = [];
    let orders = [];
    let syncStatuses = {};  // inventoryId -> [{ sellerAppName, status, time }]
    let statusChartInstance = null;
    let revenueChartInstance = null;

    // === Init ===
    function init() {
        setupNavigation();
        setupSearch();
        loadDashboard();
        loadSellerApps();
    }

    // === Navigation ===
    function setupNavigation() {
        document.querySelectorAll('.nav-item[data-tab]').forEach(item => {
            item.addEventListener('click', (e) => {
                e.preventDefault();
                switchTab(item.dataset.tab);
            });
        });
    }

    function switchTab(tabName) {
        // Update nav
        document.querySelectorAll('.nav-item[data-tab]').forEach(n => n.classList.remove('active'));
        const navItem = document.querySelector(`.nav-item[data-tab="${tabName}"]`);
        if (navItem) navItem.classList.add('active');

        // Update tab content
        document.querySelectorAll('.tab-content').forEach(t => t.classList.remove('active'));
        const tab = document.getElementById(`tab-${tabName}`);
        if (tab) tab.classList.add('active');

        // Load data on tab switch
        if (tabName === 'inventory') loadInventory();
        if (tabName === 'seller-apps') loadSellerApps();
        if (tabName === 'orders') loadOrders();
    }

    // === API Helpers ===
    async function apiGet(path) {
        const res = await fetch(`${API_BASE}${path}`);
        if (!res.ok) throw new Error(`GET ${path} failed: ${res.status}`);
        return res.json();
    }

    async function apiPost(path, body) {
        const res = await fetch(`${API_BASE}${path}`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(body)
        });
        if (!res.ok) {
            const err = await res.json().catch(() => ({}));
            throw new Error(err.message || `POST ${path} failed: ${res.status}`);
        }
        return res.json();
    }

    async function apiPut(path, body = {}) {
        const res = await fetch(`${API_BASE}${path}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(body)
        });
        if (!res.ok) {
            const err = await res.json().catch(() => ({}));
            throw new Error(err.message || `PUT ${path} failed: ${res.status}`);
        }
        return res.json();
    }

    // === Dashboard Overview ===
    async function loadDashboard() {
        try {
            // 1. Fetch all vendors
            const vendors = await apiGet('/api/vendors');
            const vendorIds = vendors.map(v => v.id);

            // 2. Fetch dashboard summaries, inventory, and orders for ALL vendors in parallel
            const [dashboardSummaries, inventoryArrays, allOrders] = await Promise.all([
                Promise.all(vendorIds.map(id => apiGet(`/api/dashboard/summary/${id}`))),
                Promise.all(vendorIds.map(id => apiGet(`/api/inventory/vendor/${id}`))),
                apiGet('/api/orders')  // all orders across all vendors
            ]);

            // 3. Aggregate dashboard summaries
            const aggregated = {
                vendorName: [...new Set(vendors.map(v => v.name))].join(', '),
                totalOrders: dashboardSummaries.reduce((s, d) => s + (d.totalOrders || 0), 0),
                pendingOrders: dashboardSummaries.reduce((s, d) => s + (d.pendingOrders || 0), 0),
                acceptedOrders: dashboardSummaries.reduce((s, d) => s + (d.acceptedOrders || 0), 0),
                fulfilledOrders: dashboardSummaries.reduce((s, d) => s + (d.fulfilledOrders || 0), 0),
                cancelledOrders: dashboardSummaries.reduce((s, d) => s + (d.cancelledOrders || 0), 0),
                totalProducts: dashboardSummaries.reduce((s, d) => s + (d.totalProducts || 0), 0),
                lowStockItems: dashboardSummaries.reduce((s, d) => s + (d.lowStockItems || 0), 0),
                lowStockAlerts: dashboardSummaries.flatMap(d => d.lowStockAlerts || []),
                totalOutlets: dashboardSummaries.reduce((s, d) => s + (d.totalOutlets || 0), 0),
                activeOutlets: dashboardSummaries.reduce((s, d) => s + (d.activeOutlets || 0), 0),
                totalSellerApps: dashboardSummaries.reduce((s, d) => s + (d.totalSellerApps || 0), 0),
                healthySellerApps: dashboardSummaries.reduce((s, d) => s + (d.healthySellerApps || 0), 0),
                vendorRating: vendors.length > 0
                    ? dashboardSummaries.reduce((s, d) => s + (d.vendorRating || 0), 0) / vendors.length
                    : 0,
                fulfillmentRate: 0
            };
            if (aggregated.totalOrders > 0) {
                aggregated.fulfillmentRate = Math.round(
                    (aggregated.acceptedOrders + aggregated.fulfilledOrders) / aggregated.totalOrders * 100 * 100
                ) / 100;
            }

            // 4. Merge all inventory across vendors
            const allInventory = inventoryArrays.flat();

            renderDashboard(aggregated);
            renderCharts(allOrders);
            renderDashboardInventory(allInventory);
            renderDashboardOrders(allOrders);
        } catch (err) {
            console.error('Failed to load dashboard:', err);
            showToast('Failed to load dashboard data', 'error');
        }
    }

    function renderDashboard(data) {
        // Vendor sidebar
        document.getElementById('vendor-name-sidebar').textContent = data.vendorName || 'Vendor';
        document.getElementById('vendor-business-sidebar').textContent = 'ONDC Vendor';
        document.getElementById('vendor-avatar').textContent = (data.vendorName || 'V')[0];
        document.getElementById('header-avatar').textContent = (data.vendorName || 'V')[0];

        // Stats cards
        document.getElementById('stat-total-orders').textContent = data.totalOrders || 0;
        document.getElementById('stat-fulfilled-orders').textContent = data.fulfilledOrders || 0;
        document.getElementById('stat-pending-orders').textContent = data.pendingOrders || 0;
        document.getElementById('stat-fulfillment-rate').textContent = (data.fulfillmentRate || 0) + '%';

        // Platform stats
        document.getElementById('stat-products').textContent = data.totalProducts || 0;
        document.getElementById('stat-outlets').textContent = `${data.activeOutlets || 0}/${data.totalOutlets || 0}`;
        document.getElementById('stat-seller-apps').textContent = `${data.healthySellerApps || 0}/${data.totalSellerApps || 0}`;
        document.getElementById('stat-rating').textContent = (data.vendorRating || 0).toFixed(1);

        // Low stock badge
        const badge = document.getElementById('low-stock-badge');
        if (data.lowStockItems > 0) {
            badge.textContent = data.lowStockItems;
            badge.style.display = 'inline';
        } else {
            badge.style.display = 'none';
        }

        // Low stock alerts
        renderLowStockAlerts(data.lowStockAlerts || []);
    }

    function renderCharts(orders) {
        // 1. Orders by Status (Doughnut)
        const statusCounts = {};
        orders.forEach(order => {
            statusCounts[order.status] = (statusCounts[order.status] || 0) + 1;
        });

        const statusCtx = document.getElementById('statusChart').getContext('2d');
        
        if (statusChartInstance) statusChartInstance.destroy();

        statusChartInstance = new Chart(statusCtx, {
            type: 'doughnut',
            data: {
                labels: Object.keys(statusCounts),
                datasets: [{
                    data: Object.values(statusCounts),
                    backgroundColor: [
                        'rgba(16, 185, 129, 0.7)',  // FULFILLED (Emerald)
                        'rgba(59, 130, 246, 0.7)',   // ACCEPTED (Blue)
                        'rgba(245, 158, 11, 0.7)',   // PENDING (Amber)
                        'rgba(244, 63, 94, 0.7)',    // CANCELLED/REJECTED (Rose)
                        'rgba(139, 92, 246, 0.7)'    // OTHER (Violet)
                    ],
                    borderColor: 'rgba(255, 255, 255, 0.1)',
                    borderWidth: 1
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: { position: 'right', labels: { color: '#94a3b8' } }
                },
                cutout: '70%'
            }
        });

        // 2. Revenue Trend (Last 7 Days)
        const last7Days = [...Array(7)].map((_, i) => {
            const d = new Date();
            d.setDate(d.getDate() - (6 - i));
            return d.toISOString().split('T')[0];
        });

        const revenueByDay = {};
        last7Days.forEach(day => revenueByDay[day] = 0);

        orders.forEach(order => {
            if (order.createdAt && order.status !== 'CANCELLED' && order.status !== 'REJECTED') {
                const day = order.createdAt.split('T')[0];
                if (revenueByDay[day] !== undefined) {
                    revenueByDay[day] += order.totalAmount || 0;
                }
            }
        });

        const revenueCtx = document.getElementById('revenueChart').getContext('2d');

        if (revenueChartInstance) revenueChartInstance.destroy();

        revenueChartInstance = new Chart(revenueCtx, {
            type: 'bar',
            data: {
                labels: last7Days.map(date => {
                    const d = new Date(date);
                    return d.toLocaleDateString('en-US', { weekday: 'short' });
                }),
                datasets: [{
                    label: 'Revenue (₹)',
                    data: Object.values(revenueByDay),
                    backgroundColor: 'rgba(59, 130, 246, 0.5)',
                    borderColor: 'rgba(59, 130, 246, 1)',
                    borderWidth: 1,
                    borderRadius: 4
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                scales: {
                    y: {
                        beginAtZero: true,
                        grid: { color: 'rgba(255, 255, 255, 0.05)' },
                        ticks: { color: '#94a3b8' }
                    },
                    x: {
                        grid: { display: false },
                        ticks: { color: '#94a3b8' }
                    }
                },
                plugins: {
                    legend: { display: false }
                }
            }
        });
    }

    function renderLowStockAlerts(alerts) {
        const container = document.getElementById('alerts-list');
        const countEl = document.getElementById('low-stock-count');

        if (alerts.length === 0) {
            countEl.textContent = 'All stock levels healthy';
            container.innerHTML = `
                <div class="empty-state" style="padding:24px">
                    <div class="empty-icon">✅</div>
                    <h3>All Good!</h3>
                    <p>No low stock items detected</p>
                </div>`;
            return;
        }

        countEl.textContent = `${alerts.length} items need attention`;
        container.innerHTML = alerts.map(alert => `
            <div class="alert-item">
                <div class="alert-icon">⚠️</div>
                <div class="alert-info">
                    <div class="alert-product">${escapeHtml(alert.productName)}</div>
                    <div class="alert-details">${escapeHtml(alert.outletName)} · SKU: ${escapeHtml(alert.productSku)}</div>
                </div>
                <div class="alert-stock">
                    <div class="alert-stock-value">${alert.availableStock}</div>
                    <div class="alert-stock-label">available</div>
                </div>
            </div>
        `).join('');
    }

    function renderDashboardInventory(items) {
        const tbody = document.getElementById('dashboard-inventory-tbody');
        const countEl = document.getElementById('dashboard-inventory-count');

        if (!items || items.length === 0) {
            countEl.textContent = 'No inventory records found';
            tbody.innerHTML = `
                <tr><td colspan="7">
                    <div class="empty-state">
                        <div class="empty-icon">📦</div>
                        <h3>No Inventory</h3>
                        <p>No inventory records found</p>
                    </div>
                </td></tr>`;
            return;
        }

        countEl.textContent = `${items.length} items across all outlets`;
        tbody.innerHTML = items.map(inv => {
            const statusBadge = inv.isLowStock
                ? '<span class="badge warning"><span class="badge-dot"></span> Low Stock</span>'
                : '<span class="badge success"><span class="badge-dot"></span> In Stock</span>';

            return `
                <tr>
                    <td><strong>${escapeHtml(inv.productName)}</strong></td>
                    <td><code style="color:var(--text-muted);font-size:12px">${escapeHtml(inv.productSku)}</code></td>
                    <td>${escapeHtml(inv.outletName)}</td>
                    <td>${inv.totalStock}</td>
                    <td>${inv.reservedStock}</td>
                    <td><strong>${inv.availableStock}</strong></td>
                    <td>${statusBadge}</td>
                </tr>`;
        }).join('');
    }

    function renderDashboardOrders(orderList) {
        const tbody = document.getElementById('dashboard-orders-tbody');
        const countEl = document.getElementById('dashboard-orders-count');

        if (!orderList || orderList.length === 0) {
            countEl.textContent = 'No orders found';
            tbody.innerHTML = `
                <tr><td colspan="7">
                    <div class="empty-state">
                        <div class="empty-icon">🛒</div>
                        <h3>No Orders Yet</h3>
                        <p>Orders will appear here when received</p>
                    </div>
                </td></tr>`;
            return;
        }

        countEl.textContent = `${orderList.length} orders across all seller apps`;
        tbody.innerHTML = orderList.map(order => {
            const statusClass = order.status === 'FULFILLED' ? 'success'
                              : order.status === 'ACCEPTED' ? 'info'
                              : order.status === 'PENDING' ? 'warning'
                              : order.status === 'CANCELLED' || order.status === 'REJECTED' ? 'danger'
                              : 'violet';

            const priorityClass = order.priority === 'CRITICAL' ? 'danger'
                                : order.priority === 'HIGH' ? 'warning'
                                : order.priority === 'MEDIUM' ? 'info' : 'success';

            const dateStr = order.createdAt ? new Date(order.createdAt).toLocaleDateString('en-IN', {
                day: 'numeric', month: 'short', hour: '2-digit', minute: '2-digit'
            }) : '—';

            const products = order.items && order.items.length
                ? order.items.map(i => escapeHtml(i.productName)).join(', ')
                : '—';

            return `
                <tr>
                    <td><code style="color:var(--accent-cyan);font-size:12px">${escapeHtml(order.ondcOrderId)}</code></td>
                    <td>${escapeHtml(order.customerName || '—')}</td>
                    <td>${products}</td>
                    <td>${order.items ? order.items.length : 0}</td>
                    <td><strong>₹${(order.totalAmount || 0).toFixed(2)}</strong></td>
                    <td><span class="badge ${statusClass}"><span class="badge-dot"></span> ${order.status}</span></td>
                    <td style="color:var(--text-muted);font-size:12px">${dateStr}</td>
                </tr>`;
        }).join('');
    }

    // === Inventory ===
    async function loadInventory() {
        try {
            const vendors = await apiGet('/api/vendors');
            const inventoryArrays = await Promise.all(vendors.map(v => apiGet(`/api/inventory/vendor/${v.id}`)));
            inventoryData = inventoryArrays.flat();
            renderInventoryTable(inventoryData);
            updateNotifications(inventoryData);
        } catch (err) {
            console.error('Failed to load inventory:', err);
            showToast('Failed to load inventory', 'error');
        }
    }

    function renderInventoryTable(items) {
        const tbody = document.getElementById('inventory-tbody');

        if (items.length === 0) {
            tbody.innerHTML = `
                <tr><td colspan="8">
                    <div class="empty-state">
                        <div class="empty-icon">📦</div>
                        <h3>No Inventory</h3>
                        <p>No inventory records found for this vendor</p>
                    </div>
                </td></tr>`;
            return;
        }

        tbody.innerHTML = items.map(inv => {
            const statusBadge = inv.isLowStock
                ? '<span class="badge warning"><span class="badge-dot"></span> Low Stock</span>'
                : '<span class="badge success"><span class="badge-dot"></span> In Stock</span>';

            const syncBadges = renderSyncBadges(inv.id);

            return `
                <tr id="inv-row-${inv.id}">
                    <td><strong>${escapeHtml(inv.productName)}</strong></td>
                    <td><code style="color:var(--text-muted);font-size:12px">${escapeHtml(inv.productSku)}</code></td>
                    <td>${escapeHtml(inv.outletName)}</td>
                    <td>
                        <input type="number" class="stock-input" id="stock-${inv.id}"
                               value="${inv.totalStock}" min="0" data-original="${inv.totalStock}">
                    </td>
                    <td>${inv.reservedStock}</td>
                    <td><strong>${inv.availableStock}</strong></td>
                    <td>${statusBadge}</td>
                    <td>
                        <button class="btn btn-success btn-sm" id="update-btn-${inv.id}"
                                onclick="app.updateStock(${inv.id}, ${inv.productId}, ${inv.outletId})">
                            Update
                        </button>
                    </td>
                </tr>`;
        }).join('');
    }

    function renderSyncBadges(inventoryId) {
        const statuses = syncStatuses[inventoryId];
        if (!statuses || statuses.length === 0) {
            return '<span style="color:var(--text-muted);font-size:11px">Not synced yet</span>';
        }
        return statuses.map(s => {
            const cls = s.syncStatus === 'SUCCESS' ? 'synced' : s.syncStatus === 'FAILED' ? 'failed' : 'pending';
            const icon = s.syncStatus === 'SUCCESS' ? '✓' : s.syncStatus === 'FAILED' ? '✗' : '⏳';
            return `<span class="sync-badge-mini ${cls}">${icon} ${escapeHtml(s.sellerAppName)}</span>`;
        }).join('');
    }

    // === Update Stock (core feature — syncs to seller apps) ===
    async function updateStock(inventoryId, productId, outletId) {
        const input = document.getElementById(`stock-${inventoryId}`);
        const btn = document.getElementById(`update-btn-${inventoryId}`);
        const newStock = parseInt(input.value);

        if (isNaN(newStock) || newStock < 0) {
            showToast('Please enter a valid stock quantity', 'error');
            return;
        }

        const originalText = btn.innerHTML;
        btn.innerHTML = '<span class="spinner spinner-sm"></span> Syncing...';
        btn.disabled = true;

        try {
            // 1. Update inventory via backend
            const result = await apiPost('/api/inventory', {
                productId: productId,
                outletId: outletId,
                totalStock: newStock
            });

            // 2. Load sync status for this inventory item
            await loadSyncStatus(result.id);

            // 3. Flash the row to indicate success
            const row = document.getElementById(`inv-row-${inventoryId}`);
            if (row) {
                row.classList.add('sync-flash');
                setTimeout(() => row.classList.remove('sync-flash'), 1000);
            }

            // 4. Update the sync badges display
            const syncBadgesEl = document.getElementById(`sync-badges-${inventoryId}`);
            if (syncBadgesEl) {
                syncBadgesEl.innerHTML = renderSyncBadges(inventoryId);
            }

            // 5. Update the input's original value
            input.dataset.original = newStock;

            // Count sync results
            const statuses = syncStatuses[inventoryId] || [];
            const successCount = statuses.filter(s => s.syncStatus === 'SUCCESS').length;
            const totalApps = statuses.length;

            showToast(
                `Stock updated to ${newStock}. Synced to ${successCount}/${totalApps} seller apps.`,
                successCount === totalApps ? 'success' : 'info'
            );

            // Refresh dashboard stats in background
            loadDashboard();

        } catch (err) {
            console.error('Failed to update stock:', err);
            showToast(`Update failed: ${err.message}`, 'error');
        } finally {
            btn.innerHTML = originalText;
            btn.disabled = false;
        }
    }

    async function loadSyncStatus(inventoryId) {
        try {
            const statuses_data = await apiGet(`/api/inventory/${inventoryId}/sync-status`);
            syncStatuses[inventoryId] = statuses_data;
        } catch (err) {
            console.error('Failed to load sync status:', err);
        }
    }

    async function syncAllInventory() {
        try {
            showToast('Syncing all inventory to seller apps...', 'info');
            const result = await apiPost(`/api/inventory/sync/${VENDOR_ID}`, {});
            showToast(`Synced ${result.length} inventory items`, 'success');
            await loadInventory();
        } catch (err) {
            console.error('Failed to sync:', err);
            showToast('Sync failed: ' + err.message, 'error');
        }
    }

    // === Seller Apps ===
    async function loadSellerApps() {
        try {
            sellerApps = await apiGet('/api/seller-apps');
            renderSellerApps(sellerApps);
        } catch (err) {
            console.error('Failed to load seller apps:', err);
            showToast('Failed to load seller apps', 'error');
        }
    }

    function renderSellerApps(apps) {
        const container = document.getElementById('seller-apps-grid');

        if (apps.length === 0) {
            container.innerHTML = `
                <div class="empty-state">
                    <div class="empty-icon">🔗</div>
                    <h3>No Seller Apps</h3>
                    <p>No seller apps connected yet</p>
                </div>`;
            return;
        }

        container.innerHTML = apps.map(app_item => {
            const statusClass = app_item.status === 'ACTIVE' ? 'success'
                              : app_item.status === 'DEGRADED' ? 'warning' : 'danger';
            const statusIcon = app_item.status === 'ACTIVE' ? '●'
                             : app_item.status === 'DEGRADED' ? '▲' : '✗';

            return `
                <div class="seller-app-card" id="app-card-${app_item.id}">
                    <div class="app-header">
                        <div>
                            <div class="app-name">${escapeHtml(app_item.name)}</div>
                            <div class="app-endpoint">${escapeHtml(app_item.apiEndpoint)}</div>
                        </div>
                        <span class="badge ${statusClass}">
                            <span class="badge-dot"></span>
                            ${app_item.status}
                        </span>
                    </div>
                    <div class="app-metrics">
                        <div class="app-metric">
                            <div class="metric-value">${app_item.responseTimeMs || 0}ms</div>
                            <div class="metric-label">Response</div>
                        </div>
                        <div class="app-metric">
                            <div class="metric-value">${app_item.uptimePercentage || 100}%</div>
                            <div class="metric-label">Uptime</div>
                        </div>
                        <div class="app-metric">
                            <div class="metric-value">${app_item.totalRequests || 0}</div>
                            <div class="metric-label">Requests</div>
                        </div>
                    </div>
                    <div class="sync-status-list" id="app-inv-${app_item.id}"></div>
                </div>`;
        }).join('');
    }

    async function checkHealth(appId) {
        const btn = document.getElementById(`health-btn-${appId}`);
        const original = btn.innerHTML;
        btn.innerHTML = '<span class="spinner spinner-sm"></span> Checking...';
        btn.disabled = true;

        try {
            const result = await apiGet(`/api/seller-apps/${appId}/health`);
            showToast(`${result.name}: ${result.status} (${result.responseTimeMs}ms)`,
                result.status === 'ACTIVE' ? 'success' : result.status === 'DEGRADED' ? 'info' : 'error');
            await loadSellerApps();
        } catch (err) {
            console.error('Health check failed:', err);
            showToast('Health check failed', 'error');
        } finally {
            btn.innerHTML = original;
            btn.disabled = false;
        }
    }

    async function toggleAppInventory(appId) {
        const container = document.getElementById(`app-inv-${appId}`);
        if (container.classList.contains('expanded')) {
            container.classList.remove('expanded');
            return;
        }

        try {
            const data = await apiGet(`/api/seller-apps/${appId}/inventory`);
            if (data.length === 0) {
                container.innerHTML = '<div style="padding:12px;color:var(--text-muted);font-size:13px;text-align:center">No inventory synced yet. Update stock in Inventory tab to trigger sync.</div>';
            } else {
                container.innerHTML = data.map(item => `
                    <div class="sync-item">
                        <div>
                            <span class="sync-product">${escapeHtml(item.productName)}</span>
                            <span class="sync-qty"> — ${item.allocatedStock} units</span>
                        </div>
                        <div>
                            <span class="sync-badge-mini ${item.syncStatus === 'SUCCESS' ? 'synced' : 'failed'}">
                                ${item.syncStatus === 'SUCCESS' ? '✓' : '✗'} ${item.syncStatus}
                            </span>
                        </div>
                    </div>
                `).join('');
            }
            container.classList.add('expanded');
        } catch (err) {
            console.error('Failed to load app inventory:', err);
            container.innerHTML = '<div style="padding:12px;color:var(--accent-rose);font-size:13px">Failed to load sync data</div>';
            container.classList.add('expanded');
        }
    }

    // === Orders ===
    async function loadOrders() {
        try {
            orders = await apiGet('/api/orders');
            renderOrders(orders);
        } catch (err) {
            console.error('Failed to load orders:', err);
            // If no orders exist, show empty state
            renderOrders([]);
        }
    }

    function renderOrders(orderList) {
        const tbody = document.getElementById('orders-tbody');

        if (orderList.length === 0) {
            tbody.innerHTML = `
                <tr><td colspan="8">
                    <div class="empty-state">
                        <div class="empty-icon">🛒</div>
                        <h3>No Orders Yet</h3>
                        <p>Orders will appear here when received</p>
                    </div>
                </td></tr>`;
            return;
        }

        tbody.innerHTML = orderList.map(order => {
            const statusClass = order.status === 'FULFILLED' ? 'success'
                              : order.status === 'ACCEPTED' ? 'info'
                              : order.status === 'PENDING' ? 'warning'
                              : order.status === 'CANCELLED' || order.status === 'REJECTED' ? 'danger'
                              : 'violet';

            const priorityClass = order.priority === 'CRITICAL' ? 'danger'
                                : order.priority === 'HIGH' ? 'warning'
                                : order.priority === 'MEDIUM' ? 'info' : 'success';

            const dateStr = order.createdAt ? new Date(order.createdAt).toLocaleDateString('en-IN', {
                day: 'numeric', month: 'short', hour: '2-digit', minute: '2-digit'
            }) : '—';

            return `
                <tr>
                    <td><code style="color:var(--accent-cyan);font-size:12px">${escapeHtml(order.ondcOrderId)}</code></td>
                    <td>${escapeHtml(order.customerName || '—')}</td>
                    <td>${order.items && order.items.length ? order.items.map(i => escapeHtml(i.productName)).join(', ') : '—'}</td>
                    <td>${order.items ? order.items.length : 0}</td>
                    <td><strong>₹${(order.totalAmount || 0).toFixed(2)}</strong></td>
                    <td><span class="badge ${statusClass}"><span class="badge-dot"></span> ${order.status}</span></td>
                    <td style="color:var(--text-muted);font-size:12px">${dateStr}</td>
                    <td>
                        ${order.status === 'PENDING' ? `
                            <div class="order-actions">
                                <button class="btn btn-accept btn-sm" onclick="app.acceptOrder(${order.id})" title="Accept Order">
                                    ✅ Accept
                                </button>
                                <button class="btn btn-reject btn-sm" onclick="app.rejectOrder(${order.id})" title="Reject Order">
                                    ❌ Reject
                                </button>
                            </div>
                        ` : `<span class="badge ${statusClass}" style="font-size:11px">${order.status}</span>`}
                    </td>
                </tr>`;
        }).join('');
    }

    // === Accept / Reject Orders ===
    async function acceptOrder(orderId) {
        if (!confirm('Accept this order? This will reserve inventory for all items.')) return;
        try {
            await apiPut(`/api/orders/${orderId}/accept`);
            showToast('Order accepted — inventory reserved successfully', 'success');
            loadOrders();        // refresh orders table
            loadDashboard();     // refresh dashboard stats
        } catch (err) {
            console.error('Failed to accept order:', err);
            showToast(err.message || 'Failed to accept order', 'error');
        }
    }

    async function rejectOrder(orderId) {
        const reason = prompt('Enter rejection reason (optional):');
        if (reason === null) return;  // user cancelled the prompt
        try {
            await apiPut(`/api/orders/${orderId}/reject`, { reason: reason || 'No reason provided' });
            showToast('Order rejected', 'error');
            loadOrders();
            loadDashboard();
        } catch (err) {
            console.error('Failed to reject order:', err);
            showToast(err.message || 'Failed to reject order', 'error');
        }
    }

    // === Toast Notifications ===
    function showToast(message, type = 'info') {
        const container = document.getElementById('toast-container');
        const icons = { success: '✅', error: '❌', info: 'ℹ️' };

        const toast = document.createElement('div');
        toast.className = `toast ${type}`;
        toast.innerHTML = `
            <span class="toast-icon">${icons[type] || icons.info}</span>
            <span class="toast-message">${escapeHtml(message)}</span>
            <button class="toast-close" onclick="this.parentElement.remove()">×</button>
        `;
        container.appendChild(toast);

        setTimeout(() => {
            if (toast.parentElement) toast.remove();
        }, 4000);
    }

    // === Utils ===
    function escapeHtml(str) {
        if (!str) return '';
        const div = document.createElement('div');
        div.textContent = str;
        return div.innerHTML;
    }

    // === Add Inventory Modal ===
    function openAddInventoryModal() {
        document.getElementById('add-inventory-modal').style.display = 'flex';
        document.getElementById('add-inventory-form').reset();
    }

    function closeAddInventoryModal() {
        document.getElementById('add-inventory-modal').style.display = 'none';
    }

    async function submitAddInventory(e) {
        e.preventDefault();
        const productId = parseInt(document.getElementById('add-product-id').value);
        const outletId = parseInt(document.getElementById('add-outlet-id').value);
        const totalStock = parseInt(document.getElementById('add-total-stock').value);
        const reorderLevel = document.getElementById('add-reorder-level').value
            ? parseInt(document.getElementById('add-reorder-level').value)
            : null;

        if (isNaN(productId) || isNaN(outletId) || isNaN(totalStock)) {
            showToast('Please fill in all required fields', 'error');
            return;
        }

        const btn = document.getElementById('add-inventory-submit-btn');
        const originalText = btn.innerHTML;
        btn.innerHTML = '<span class="spinner spinner-sm"></span> Adding...';
        btn.disabled = true;

        try {
            const payload = { productId, outletId, totalStock };
            if (reorderLevel !== null) payload.reorderLevel = reorderLevel;

            await apiPost('/api/inventory', payload);
            showToast(`Inventory added for product ${productId} at outlet ${outletId}`, 'success');
            closeAddInventoryModal();
            await loadInventory();
            loadDashboard();
        } catch (err) {
            console.error('Failed to add inventory:', err);
            showToast(`Failed to add inventory: ${err.message}`, 'error');
        } finally {
            btn.innerHTML = originalText;
            btn.disabled = false;
        }
    }

    // === Notifications ===
    function updateNotifications(items) {
        const lowStockItems = items.filter(item => item.availableStock < 5);
        const count = lowStockItems.length;
        const countEl = document.getElementById('notification-count');
        const listEl = document.getElementById('notification-list');

        // Update badge
        countEl.textContent = count;
        countEl.style.display = count > 0 ? 'flex' : 'none';

        // Update dropdown list
        if (count === 0) {
            listEl.innerHTML = '<div class="empty-notifications">No new notifications</div>';
        } else {
            listEl.innerHTML = lowStockItems.map(item => `
                <div class="notification-item" onclick="app.switchTab('inventory')">
                    <div class="notif-icon">⚠️</div>
                    <div class="notif-content">
                        <div class="notif-title">Low Stock Alert</div>
                        <div class="notif-desc">
                            <strong>${escapeHtml(item.productName)}</strong> is running low (${item.availableStock} remaining) at ${escapeHtml(item.outletName)}.
                        </div>
                        <div class="notif-time">Just now</div>
                    </div>
                </div>
            `).join('');
        }
    }

    function toggleNotifications() {
        const dropdown = document.getElementById('notification-dropdown');
        dropdown.classList.toggle('active');
        
        // Close when clicking outside
        if (dropdown.classList.contains('active')) {
            document.addEventListener('click', closeNotificationsOutside);
        } else {
            document.removeEventListener('click', closeNotificationsOutside);
        }
    }

    function closeNotificationsOutside(e) {
        const wrapper = document.querySelector('.notification-wrapper');
        if (!wrapper.contains(e.target)) {
            document.getElementById('notification-dropdown').classList.remove('active');
            document.removeEventListener('click', closeNotificationsOutside);
        }
    }

    function clearNotifications(e) {
        e.stopPropagation();
        // For demo, just hide the badge temporarily or clear list
        // In real app, mark as read in backend
        document.getElementById('notification-count').style.display = 'none';
        document.getElementById('notification-list').innerHTML = '<div class="empty-notifications">No new notifications</div>';
        showToast('Notifications cleared', 'default');
    }

    // === Global Search ===
    function setupSearch() {
        const input = document.getElementById('global-search');
        const dropdown = document.getElementById('search-results');
        let debounceTimer = null;

        input.addEventListener('input', () => {
            clearTimeout(debounceTimer);
            const query = input.value.trim().toLowerCase();
            if (query.length < 2) {
                dropdown.style.display = 'none';
                return;
            }
            debounceTimer = setTimeout(() => performSearch(query), 250);
        });

        input.addEventListener('focus', () => {
            const query = input.value.trim().toLowerCase();
            if (query.length >= 2) performSearch(query);
        });

        // Close dropdown on outside click
        document.addEventListener('click', (e) => {
            if (!e.target.closest('.header-search')) {
                dropdown.style.display = 'none';
            }
        });
    }

    async function performSearch(query) {
        const dropdown = document.getElementById('search-results');
        const results = [];

        // Search orders (already loaded or fetch)
        try {
            if (!orders || orders.length === 0) {
                orders = await apiGet(`/api/orders/vendor/${VENDOR_ID}`);
            }
            orders.forEach(order => {
                const matchFields = [
                    order.ondcOrderId,
                    order.customerName,
                    order.sellerAppName,
                    order.status,
                    ...(order.items || []).map(i => i.productName)
                ].filter(Boolean).join(' ').toLowerCase();

                if (matchFields.includes(query)) {
                    results.push({
                        type: 'order',
                        icon: '🛒',
                        title: order.ondcOrderId,
                        subtitle: `${order.customerName || '—'} · ₹${(order.totalAmount || 0).toFixed(2)} · ${order.status}`,
                        tab: 'orders'
                    });
                }
            });
        } catch (e) { /* ignore */ }

        // Search inventory
        try {
            if (!inventoryData || inventoryData.length === 0) {
                inventoryData = await apiGet(`/api/inventory/vendor/${VENDOR_ID}`);
            }
            inventoryData.forEach(inv => {
                const matchFields = [
                    inv.productName,
                    inv.productSku,
                    inv.outletName
                ].filter(Boolean).join(' ').toLowerCase();

                if (matchFields.includes(query)) {
                    results.push({
                        type: 'inventory',
                        icon: '📦',
                        title: inv.productName,
                        subtitle: `SKU: ${inv.productSku || '—'} · Stock: ${inv.availableStock ?? inv.totalStock} · ${inv.outletName || ''}`,
                        tab: 'inventory',
                        searchQuery: query
                    });
                }
            });
        } catch (e) { /* ignore */ }

        // Search seller apps
        try {
            if (!sellerApps || sellerApps.length === 0) {
                sellerApps = await apiGet(`/api/seller-apps/vendor/${VENDOR_ID}`);
            }
            sellerApps.forEach(sa => {
                const matchFields = [
                    sa.name,
                    sa.apiEndpoint,
                    sa.status
                ].filter(Boolean).join(' ').toLowerCase();

                if (matchFields.includes(query)) {
                    results.push({
                        type: 'seller-app',
                        icon: '🔗',
                        title: sa.name,
                        subtitle: `${sa.status} · ${sa.apiEndpoint || ''}`,
                        tab: 'seller-apps'
                    });
                }
            });
        } catch (e) { /* ignore */ }

        // Render results
        if (results.length === 0) {
            dropdown.innerHTML = '<div class="search-no-results">No results found</div>';
        } else {
            dropdown.innerHTML = results.slice(0, 10).map(r => {
                const clickAction = r.searchQuery
                    ? `app.filterInventory('${r.searchQuery.replace(/'/g, "\\'")}')`
                    : `app.switchTab('${r.tab}')`;
                return `
                <div class="search-result-item" onclick="${clickAction}; document.getElementById('search-results').style.display='none';">
                    <span class="search-result-icon">${r.icon}</span>
                    <div class="search-result-text">
                        <div class="search-result-title">${escapeHtml(r.title)}</div>
                        <div class="search-result-subtitle">${escapeHtml(r.subtitle)}</div>
                    </div>
                    <span class="search-result-type">${r.type}</span>
                </div>
            `}).join('');
        }
        dropdown.style.display = 'block';
    }

    // === Inventory Filter from Search ===
    function filterInventory(query) {
        // Switch tab UI without triggering loadInventory
        document.querySelectorAll('.nav-item[data-tab]').forEach(n => n.classList.remove('active'));
        const navItem = document.querySelector('.nav-item[data-tab="inventory"]');
        if (navItem) navItem.classList.add('active');
        document.querySelectorAll('.tab-content').forEach(t => t.classList.remove('active'));
        const tab = document.getElementById('tab-inventory');
        if (tab) tab.classList.add('active');

        const doFilter = () => {
            const filtered = inventoryData.filter(inv => {
                const matchFields = [
                    inv.productName,
                    inv.productSku,
                    inv.outletName
                ].filter(Boolean).join(' ').toLowerCase();
                return matchFields.includes(query.toLowerCase());
            });
            renderInventoryTable(filtered);
            // Show filter banner
            const banner = document.getElementById('inventory-filter-banner');
            const bannerText = document.getElementById('inventory-filter-text');
            bannerText.textContent = `Showing ${filtered.length} result${filtered.length !== 1 ? 's' : ''} for "${query}"`;
            banner.style.display = 'flex';
        };

        if (inventoryData && inventoryData.length > 0) {
            doFilter();
        } else {
            apiGet(`/api/inventory/vendor/${VENDOR_ID}`).then(data => {
                inventoryData = data;
                doFilter();
            });
        }
    }

    function clearInventoryFilter() {
        document.getElementById('inventory-filter-banner').style.display = 'none';
        document.getElementById('global-search').value = '';
        renderInventoryTable(inventoryData);
    }

    // === Public API ===
    return {
        init,
        switchTab,
        updateStock,
        syncAllInventory,
        checkHealth,
        toggleAppInventory,
        openAddInventoryModal,
        closeAddInventoryModal,
        submitAddInventory,
        toggleNotifications,
        clearNotifications,
        acceptOrder,
        rejectOrder,
        filterInventory,
        clearInventoryFilter,
        showToast
    };
})();

// Start the app when DOM is ready
document.addEventListener('DOMContentLoaded', app.init);
