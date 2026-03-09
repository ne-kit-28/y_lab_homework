import React from 'react'

export default function LogoutButton() {
  const handleLogout = () => {
    try {
      localStorage.removeItem('token')
      localStorage.removeItem('userId')
      localStorage.removeItem('userEmail')
      localStorage.removeItem('admin_token')
      localStorage.removeItem('admin_userId')
    } catch {
      // ignore
    }
    window.location.href = '/'
  }

  return (
    <button onClick={handleLogout} style={logoutStyle} aria-label="Log out">
      🚪 Log Out
    </button>
  )
}

const logoutStyle = {
  padding: '10px 16px',
  borderRadius: 10,
  border: '1px solid rgba(255,255,255,0.2)',
  background: 'rgba(255,255,255,0.1)',
  color: '#fff',
  cursor: 'pointer',
  fontWeight: 500,
  transition: 'all 0.2s'
}
