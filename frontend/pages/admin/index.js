import { useEffect, useState } from 'react'

export default function AdminDashboard() {
  const [mounted, setMounted] = useState(false)
  const [users, setUsers] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [selectedUser, setSelectedUser] = useState(null)
  const [auditLogs, setAuditLogs] = useState([])
  const [loadingAudit, setLoadingAudit] = useState(false)

  useEffect(() => {
    setMounted(true)
  }, [])

  useEffect(() => {
    if (!mounted) return
    
    const adminToken = localStorage.getItem('admin_token')
    if (!adminToken) {
      window.location.href = '/admin/login'
      return
    }
    fetchUsers()
  }, [mounted])

  const fetchUsers = async () => {
    try {
      setLoading(true)
      const res = await fetch('http://localhost:8080/admin/user/all', {
        headers: {
          'Authorization': 'Bearer ' + localStorage.getItem('admin_token')
        }
      })
      if (res.ok) {
        const data = await res.json()
        setUsers(Array.isArray(data) ? data : [])
      } else {
        setError('Failed to load users')
      }
    } catch (e) {
      setError('Error loading users')
    } finally {
      setLoading(false)
    }
  }

  const handleBlockToggle = async (userId, currentStatus) => {
    const newStatus = !currentStatus
    if (!confirm(`Are you sure you want to ${newStatus ? 'block' : 'unblock'} this user?`)) return
    
    try {
      const res = await fetch(`http://localhost:8080/admin/user/${userId}/block/${newStatus}`, {
        headers: {
          'Authorization': 'Bearer ' + localStorage.getItem('admin_token')
        }
      })
      if (res.ok) {
        fetchUsers()
      } else {
        alert('Failed to update user status')
      }
    } catch {
      alert('Error updating user')
    }
  }

  const handleViewAudit = async (userId) => {
    setSelectedUser(userId)
    setLoadingAudit(true)
    try {
      const res = await fetch(`http://localhost:8080/admin/user/${userId}/audit`, {
        headers: {
          'Authorization': 'Bearer ' + localStorage.getItem('admin_token')
        }
      })
      if (res.ok) {
        const data = await res.json()
        setAuditLogs(Array.isArray(data) ? data : [])
      } else {
        setAuditLogs([])
      }
    } catch {
      setAuditLogs([])
    } finally {
      setLoadingAudit(false)
    }
  }

  const closeAuditModal = () => {
    setSelectedUser(null)
    setAuditLogs([])
  }

  if (!mounted) {
    return (
      <div style={{ ...pageContainer, display: 'flex', alignItems: 'center', justifyContent: 'center', minHeight: '100vh' }}>
        <div style={{ color: '#fff' }}>Loading...</div>
      </div>
    )
  }

  return (
    <div style={pageContainer}>
      <style>{`
        * { box-sizing: border-box; margin: 0; padding: 0; }
        body { font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif; background: #1a1a2e; }
      `}</style>
      
      <div style={header}>
        <div style={headerContent}>
          <h1 style={title}>🛡️ Admin Panel</h1>
          <div style={headerActions}>
            <button onClick={() => window.location.href = '/dashboard'} style={backButton}>
              ← User App
            </button>
            <button onClick={() => {
              localStorage.removeItem('admin_token')
              localStorage.removeItem('admin_userId')
              window.location.href = '/admin/login'
            }} style={backButton}>
              🚪 Log Out
            </button>
          </div>
        </div>
      </div>

      <div style={content}>
        <div style={section}>
          <h2 style={sectionTitle}>All Users</h2>
          
          {loading && <div style={loadingStyle}>Loading users...</div>}
          {error && <div style={errorBox}>{error}</div>}
          
          {!loading && users.length === 0 && (
            <div style={emptyState}>No users found</div>
          )}

          <div style={tableContainer}>
            <table style={table}>
              <thead>
                <tr>
                  <th style={th}>ID</th>
                  <th style={th}>Name</th>
                  <th style={th}>Email</th>
                  <th style={th}>Role</th>
                  <th style={th}>Status</th>
                  <th style={th}>Actions</th>
                </tr>
              </thead>
              <tbody>
                {users.map(user => (
                  <tr key={user.id} style={tr}>
                    <td style={td}>{user.id}</td>
                    <td style={td}>{user.name || 'N/A'}</td>
                    <td style={td}>{user.email || 'N/A'}</td>
                    <td style={td}>
                      <span style={roleBadge(user.role)}>{user.role || 'N/A'}</span>
                    </td>
                    <td style={td}>
                      <span style={statusBadge(user.blocked)}>
                        {user.blocked ? '🔴 Blocked' : '🟢 Active'}
                      </span>
                    </td>
                    <td style={td}>
                      <div style={actionButtons}>
                        <button 
                          onClick={() => handleViewAudit(user.id)}
                          style={auditButton}
                        >
                          📋 Audit
                        </button>
                        <button 
                          onClick={() => handleBlockToggle(user.id, user.blocked)}
                          style={user.blocked ? unblockButton : blockButton}
                        >
                          {user.blocked ? '✓ Unblock' : '✕ Block'}
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      </div>

      {selectedUser && (
        <div style={modalOverlay} onClick={closeAuditModal}>
          <div style={modalContent} onClick={e => e.stopPropagation()}>
            <div style={modalHeader}>
              <h3 style={modalTitle}>📋 Audit Logs for User #{selectedUser}</h3>
              <button onClick={closeAuditModal} style={closeBtn}>×</button>
            </div>
            <div style={modalBody}>
              {loadingAudit ? (
                <div style={loadingStyle}>Loading audit logs...</div>
              ) : auditLogs.length === 0 ? (
                <div style={emptyState}>No audit logs found</div>
              ) : (
                <div style={logList}>
                  {auditLogs.map((log, index) => (
                    <div key={index} style={logItem}>
                      <div style={logAction}>{log.action || 'Unknown action'}</div>
                      <div style={logDetails}>
                        <span>👤 User: {log.userId}</span>
                        <span>📅 {log.timestamp || 'No date'}</span>
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </div>
          </div>
        </div>
      )}
    </div>
  )
}

const pageContainer = {
  minHeight: '100vh',
  background: '#1a1a2e',
  color: '#fff'
}

const header = {
  background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
  padding: '20px 0',
  boxShadow: '0 4px 20px rgba(0,0,0,.3)'
}

const headerContent = {
  maxWidth: 1200,
  margin: '0 auto',
  padding: '0 20px',
  display: 'flex',
  justifyContent: 'space-between',
  alignItems: 'center'
}

const title = {
  margin: 0,
  fontSize: 28,
  fontWeight: 700
}

const headerActions = {
  display: 'flex',
  gap: 12,
  alignItems: 'center'
}

const backButton = {
  padding: '10px 16px',
  borderRadius: 8,
  border: '1px solid rgba(255,255,255,0.3)',
  background: 'rgba(255,255,255,0.1)',
  color: '#fff',
  cursor: 'pointer',
  fontSize: 14
}

const content = {
  maxWidth: 1200,
  margin: '0 auto',
  padding: '40px 20px'
}

const section = {
  background: '#16213e',
  borderRadius: 16,
  padding: 24,
  boxShadow: '0 8px 32px rgba(0,0,0,.2)'
}

const sectionTitle = {
  margin: '0 0 20px 0',
  fontSize: 22,
  fontWeight: 600
}

const loadingStyle = {
  textAlign: 'center',
  padding: 40,
  color: '#888'
}

const errorBox = {
  padding: '12px 16px',
  background: '#ffebee',
  color: '#c62828',
  borderRadius: 8,
  marginBottom: 16
}

const emptyState = {
  textAlign: 'center',
  padding: 40,
  color: '#888',
  background: '#0f3460',
  borderRadius: 12
}

const tableContainer = {
  overflowX: 'auto'
}

const table = {
  width: '100%',
  borderCollapse: 'collapse'
}

const th = {
  textAlign: 'left',
  padding: '14px 12px',
  borderBottom: '2px solid #0f3460',
  color: '#888',
  fontWeight: 600,
  fontSize: 13,
  textTransform: 'uppercase'
}

const td = {
  padding: '14px 12px',
  borderBottom: '1px solid #0f3460'
}

const tr = {
  transition: 'background 0.2s'
}

const roleBadge = (role) => ({
  padding: '4px 10px',
  borderRadius: 12,
  background: role === 'ADMINISTRATOR' ? 'linear-gradient(135deg, #f093fb, #f5576c-gradient(135deg)' : 'linear, #4facfe, #00f2fe)',
  color: '#fff',
  fontSize: 12,
  fontWeight: 500
})

const statusBadge = (blocked) => ({
  padding: '4px 10px',
  borderRadius: 12,
  background: blocked ? '#e57373' : '#4caf50',
  color: '#fff',
  fontSize: 12,
  fontWeight: 500
})

const actionButtons = {
  display: 'flex',
  gap: 8
}

const auditButton = {
  padding: '6px 12px',
  borderRadius: 6,
  border: '1px solid #4facfe',
  background: 'transparent',
  color: '#4facfe',
  cursor: 'pointer',
  fontSize: 12
}

const blockButton = {
  padding: '6px 12px',
  borderRadius: 6,
  border: '1px solid #e57373',
  background: 'transparent',
  color: '#e57373',
  cursor: 'pointer',
  fontSize: 12
}

const unblockButton = {
  padding: '6px 12px',
  borderRadius: 6,
  border: '1px solid #4caf50',
  background: 'transparent',
  color: '#4caf50',
  cursor: 'pointer',
  fontSize: 12
}

const modalOverlay = {
  position: 'fixed',
  left: 0,
  top: 0,
  right: 0,
  bottom: 0,
  background: 'rgba(0,0,0,0.7)',
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'center',
  zIndex: 1000
}

const modalContent = {
  background: '#16213e',
  borderRadius: 16,
  width: '90%',
  maxWidth: 600,
  maxHeight: '80vh',
  overflow: 'hidden',
  display: 'flex',
  flexDirection: 'column'
}

const modalHeader = {
  display: 'flex',
  justifyContent: 'space-between',
  alignItems: 'center',
  padding: '20px 24px',
  borderBottom: '1px solid #0f3460'
}

const modalTitle = {
  margin: 0,
  fontSize: 18
}

const closeBtn = {
  background: 'none',
  border: 'none',
  color: '#888',
  fontSize: 28,
  cursor: 'pointer'
}

const modalBody = {
  padding: 20,
  overflowY: 'auto'
}

const logList = {
  display: 'flex',
  flexDirection: 'column',
  gap: 12
}

const logItem = {
  padding: 14,
  background: '#0f3460',
  borderRadius: 10
}

const logAction = {
  fontWeight: 600,
  marginBottom: 6,
  color: '#4facfe'
}

const logDetails = {
  display: 'flex',
  gap: 16,
  fontSize: 13,
  color: '#888'
}
