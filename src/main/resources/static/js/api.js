const Api = (() => {
  const baseUrl = '/api';
  const json = async (r) => { 
    if(!r.ok) {
      const errorText = await r.text();
      throw new Error(`Request failed (${r.status}): ${errorText || r.statusText}`); 
    } 
    return r.json(); 
  };

  return {
    // FAQ
    listFaqs: (q='') => fetch(`${baseUrl}/faqs${q?`?q=${encodeURIComponent(q)}`:''}`).then(json),
    createFaq: (faq) => fetch(`${baseUrl}/faqs`, {method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify(faq)}).then(json),
    updateFaq: (id,faq) => fetch(`${baseUrl}/faqs/${id}`, {method:'PUT',headers:{'Content-Type':'application/json'},body:JSON.stringify(faq)}).then(json),
    deleteFaq: (id) => fetch(`${baseUrl}/faqs/${id}`, {method:'DELETE'}).then(r=>({ok:r.ok})),

    // Tickets
    listTickets: () => fetch(`${baseUrl}/tickets`)
        .then(response => {
            console.log('Tickets API response status:', response.status);
            if (!response.ok) {
                throw new Error(`HTTP ${response.status}: ${response.statusText}`);
            }
            return response.json();
        })
        .then(data => {
            console.log('Tickets API response data:', data);
            return data;
        }),
    myTickets: (userId) => fetch(`${baseUrl}/tickets?studentId=${userId}`).then(json),
    createTicket: (t) => fetch(`${baseUrl}/tickets`, {method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify(t)}).then(json),
    updateTicket: (id,t) => fetch(`${baseUrl}/tickets/${id}`, {method:'PUT',headers:{'Content-Type':'application/json'},body:JSON.stringify(t)}).then(json),
    deleteTicket: (id) => fetch(`${baseUrl}/tickets/${id}`, {method:'DELETE'}).then(r=>({ok:r.ok})),

    // Ticket Replies
    getTicketReplies: (ticketId) => fetch(`${baseUrl}/ticket-replies/ticket/${ticketId}`).then(json),
    createTicketReply: (reply) => fetch(`${baseUrl}/ticket-replies`, {method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify(reply)}).then(json),
    deleteTicketReply: (id) => fetch(`${baseUrl}/ticket-replies/${id}`, {method:'DELETE'}).then(r=>({ok:r.ok})),

    // Users
    me: () => JSON.parse(localStorage.getItem('demoUser') || 'null'),
    logout: () => { localStorage.removeItem('demoUser'); location.href='/login.html'; },
    listUsers: (role) => fetch(`${baseUrl}/users${role?`?role=${encodeURIComponent(role)}`:''}`).then(json),
    updateUser: (id,u) => fetch(`${baseUrl}/users/${id}`, {method:'PUT',headers:{'Content-Type':'application/json'},body:JSON.stringify(u)}).then(json),
    deleteUser: (id) => fetch(`${baseUrl}/users/${id}`, {method:'DELETE'}).then(r=>({ok:r.ok})),

    // Feedback
    listFeedback: () => fetch(`${baseUrl}/feedback`).then(json),
    createFeedback: (f) => fetch(`${baseUrl}/feedback`, {method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify(f)}).then(json),
    updateFeedback: (id,f) => fetch(`${baseUrl}/feedback/${id}`, {method:'PUT',headers:{'Content-Type':'application/json'},body:JSON.stringify(f)}).then(json),
    deleteFeedback: (id) => fetch(`${baseUrl}/feedback/${id}`, {method:'DELETE'}).then(r=>({ok:r.ok})),

    // Events
    listEvents: (params = '') => fetch(`${baseUrl}/events${params}`).then(json),
    createEvent: (e) => fetch(`${baseUrl}/events`, {method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify(e)}).then(json),
    updateEvent: (id,e) => fetch(`${baseUrl}/events/${id}`, {method:'PUT',headers:{'Content-Type':'application/json'},body:JSON.stringify(e)}).then(json),
    deleteEvent: (id) => fetch(`${baseUrl}/events/${id}`, {method:'DELETE'}).then(r=>({ok:r.ok})),

    // Event Requests
    listEventRequests: () => fetch(`${baseUrl}/event-requests`).then(json),
    submitEventRequest: (req) => fetch(`${baseUrl}/event-requests`, {method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify(req)}).then(json),
    approveEventRequest: (id, adminId) => fetch(`${baseUrl}/event-requests/${id}/approve?adminId=${adminId}`, {method:'POST'}).then(json),
    rejectEventRequest: (id) => fetch(`${baseUrl}/event-requests/${id}/reject`, {method:'POST'}).then(json),

    // Resources
    listResources: (params = '') => fetch(`${baseUrl}/resources${params}`).then(json),
    getResource: (id) => fetch(`${baseUrl}/resources/${id}`).then(json),
    createResource: (r) => fetch(`${baseUrl}/resources`, {method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify(r)}).then(json),
    updateResource: (id, r) => fetch(`${baseUrl}/resources/${id}`, {method:'PUT',headers:{'Content-Type':'application/json'},body:JSON.stringify(r)}).then(json),
    updateResourceStatus: (id, status) => fetch(`${baseUrl}/resources/${id}/status`, {method:'PUT',headers:{'Content-Type':'application/json'},body:JSON.stringify({status})}).then(json),
    deleteResource: (id) => fetch(`${baseUrl}/resources/${id}`, {method:'DELETE'}).then(r=>({ok:r.ok})),
    getResourceCategories: () => fetch(`${baseUrl}/resources/categories`).then(json),
    getResourceLocations: () => fetch(`${baseUrl}/resources/locations`).then(json),

    // Resource Requests
    listResourceRequests: (params = '') => fetch(`${baseUrl}/resource-requests${params}`).then(json),
    getResourceRequest: (id) => fetch(`${baseUrl}/resource-requests/${id}`).then(json),
    submitResourceRequest: (req) => fetch(`${baseUrl}/resource-requests`, {method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify(req)}).then(json),
    approveResourceRequest: (id, adminId, comments = '') => fetch(`${baseUrl}/resource-requests/${id}/approve?adminId=${adminId}`, {method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({comments})}).then(json),
    rejectResourceRequest: (id, adminId, comments = '') => fetch(`${baseUrl}/resource-requests/${id}/reject?adminId=${adminId}`, {method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({comments})}).then(json),
    returnResource: (id) => fetch(`${baseUrl}/resource-requests/${id}/return`, {method:'POST'}).then(json),
    getUserResourceRequests: (userId) => fetch(`${baseUrl}/resource-requests/user/${userId}`).then(json),

    // Issue Reports
    listIssueReports: (params = '') => fetch(`${baseUrl}/issue-reports${params}`).then(json),
    getIssueReport: (id) => fetch(`${baseUrl}/issue-reports/${id}`).then(json),
    submitIssueReport: (report) => fetch(`${baseUrl}/issue-reports`, {method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify(report)}).then(json),
    updateIssueReportStatus: (id, status, comments = '') => fetch(`${baseUrl}/issue-reports/${id}/status`, {method:'PUT',headers:{'Content-Type':'application/json'},body:JSON.stringify({status, comments})}).then(json),
    deleteIssueReport: (id) => fetch(`${baseUrl}/issue-reports/${id}`, {method:'DELETE'}).then(r=>({ok:r.ok})),
    getUserIssueReports: (userId) => fetch(`${baseUrl}/issue-reports/user/${userId}`).then(json),

    // Bookings
    listBookings: (userId) => fetch(`${baseUrl}/bookings?userId=${userId}`).then(json),
    createBooking: (b) => fetch(`${baseUrl}/bookings`, {method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify(b)}).then(json),
    cancelBooking: (id) => fetch(`${baseUrl}/bookings/${id}`, {method:'DELETE'}).then(r=>({ok:r.ok}))
  };
})();

const UI = {
  setActive: (id) => {
    document.querySelectorAll('.link').forEach(a=>a.classList.remove('active'));
    const el = document.getElementById(id); if(el) el.classList.add('active');
  },
  renderNav: () => {
    const user = Api.me();
    const links = document.querySelector('.nav .links'); if(!links) return;
    links.innerHTML = ''+
      `<a class="link" id="nav-home" href="/index.html">Home</a>`+
      `<a class="link" id="nav-faq" href="/${user && (user.role==='ADMIN' || user.role==='SUPER_ADMIN' || user.role==='FACULTY' || user.role==='IT_SUPPORT' || user.role==='COMM_SERVICE')?'faq-admin':'faq-user'}.html">FAQs</a>`+
      (user && user.role==='STUDENT'?`<a class="link" id="nav-tickets" href="/tickets.html">Tickets</a>`:'')+
      (user?`<a class="link" id="nav-profile" href="/${user.role==='STUDENT'?'seeuser-profile':'user-profile'}.html">${user.role==='STUDENT'?'SeeUser-Profile':'User-Profile'}</a>`:'')+
      (user && (user.role==='ADMIN' || user.role==='SUPER_ADMIN' || user.role==='FACULTY' || user.role==='IT_SUPPORT' || user.role==='COMM_SERVICE')?`<a class="link" id="nav-user-admin" href="/user-admin.html">User-Admin</a>`:'')+
      (user && (user.role==='ADMIN' || user.role==='SUPER_ADMIN')?`<a class="link" id="nav-super" href="/superadmin.html">Super Admin</a>`:'')+
      (user && (user.role==='ADMIN' || user.role==='SUPER_ADMIN' || user.role==='FACULTY' || user.role==='IT_SUPPORT' || user.role==='COMM_SERVICE')?`<a class="link" id="nav-feedback" href="/feedback-admin.html">Feedback</a>`:'')+
      (user && user.role==='STUDENT'?`<a class="link" id="nav-feedback" href="/feedback-user.html">Feedback</a>`:'')+
      (user && (user.role==='ADMIN' || user.role==='SUPER_ADMIN' || user.role==='FACULTY' || user.role==='IT_SUPPORT' || user.role==='COMM_SERVICE')?`<a class="link" id="nav-events" href="/event-management.html">Event Management</a>`:'')+
      (user && user.role==='STUDENT'?`<a class="link" id="nav-events" href="/events-user.html">Events</a>`:'')+
      (user && (user.role==='ADMIN' || user.role==='SUPER_ADMIN' || user.role==='FACULTY' || user.role==='IT_SUPPORT' || user.role==='COMM_SERVICE')?`<a class="link" id="nav-resources" href="/resource-management.html">Resource Management</a>`:'')+
      (user && user.role==='STUDENT'?`<a class="link" id="nav-resources" href="/resources-user.html">Resources</a>`:'')+
      (user && (user.role==='ADMIN' || user.role==='SUPER_ADMIN' || user.role==='FACULTY' || user.role==='IT_SUPPORT' || user.role==='COMM_SERVICE')?`<a class="link" id="nav-ticket-mgmt" href="/ticket-management.html">Ticket Management</a>`:'')+
      (user && (user.role==='ADMIN' || user.role==='SUPER_ADMIN' || user.role==='DEPT_HEAD')?`<a class="link" id="nav-dept" href="/department-dashboard.html">Department</a>`:'')+
      `<a class="link" id="nav-logout" href="javascript:void(0)">Logout</a>`;
    const logout = document.getElementById('nav-logout'); if(logout) logout.addEventListener('click', Api.logout);
  },
  statusBadge: (s) => ({
    PENDING: '<span class="badge pending">Pending</span>',
    IN_PROGRESS: '<span class="badge progress">In Progress</span>',
    RESOLVED: '<span class="badge resolved">Resolved</span>',
    CLOSED: '<span class="badge closed">Closed</span>'
  })[s] || s
};

