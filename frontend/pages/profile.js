import { useEffect, useState } from 'react'
import apiFetch from '../utils/api.js'
import LogoutButton from '../components/LogoutButton'

export default function Profile() {
  const [mounted, setMounted] = useState(false)
  const [user, setUser] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [editing, setEditing] = useState(false)
  const [name, setName] = useState('')
  const [email, setEmail] = useState('')
  const [success, setSuccess] = useState(null)

  useEffect(() => {
    setMounted(true)
  }, [])

  const userId = mounted ? localStorage.getItem('userId') : null
  const token = mounted ? localStorage.getItem('token') : null
  const storedEmail = mounted ? localStorage.getItem('userEmail') : null

  useEffect(() => {
    if (!mounted) return
    if (!token || !userId) {
      window.location.href = '/'
      return
    }
    fetchUser()
  }, [mounted, userId, token])

  const fetchUser = async () => {
    if (!userId) return
    try {
      setLoading(true)
      if (storedEmail) {
        setEmail(storedEmail)
        const res = await apiFetch(`/user/${userId}/${storedEmail}`)
        if (res.ok) {
          const data = await res.json()
          setUser(data)
          setName(data.name || '')
        }
      } else {
        const res = await apiFetch(`/user/${userId}/all`)
        if (res.ok) {
          const data = await res.json()
          if (Array.isArray(data) && data.length > 0) {
            setUser(data[0])
            setName(data[0].name || '')
            setEmail(data[0].email || '')
          }
        }
      }
    } catch (e) {
      setError('Failed to load user data')
    } finally {
      setLoading(false)
    }
  }

  const handleUpdate = async (e) => {
    e.preventDefault()
    try {
      setError(null)
      setSuccess(null)
      const res = await apiFetch(`/user/${userId}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ name, email })
      })
      if (res.ok) {
        setSuccess('Profile updated successfully!')
        setEditing(false)
        localStorage.setItem('userEmail', email)
      } else {
        setError('Failed to update profile')
      }
    } catch {
      setError('Failed to update profile')
    }
  }

  const handleDelete = async () => {
    if (!confirm('Are you sure you want to delete your account? This cannot be undone.')) return
    if (!confirm('Really delete account?')) return
    try {
      const res = await apiFetch(`/user/${userId}`, { method: 'DELETE' })
      if (res.ok) {
        localStorage.clear()
        window.location.href = '/'
      } else {
        alert('Failed to delete account')
      }
    } catch {
      alert('Failed to delete account')
    }
  }

  if (!mounted) {
    return (
      <div style={pageContainer}>
        <div style={loadingBox}>Loading...</div>
      </div>
    )
  }

  if (loading) {
    return (
      <div style={pageContainer}>
        <div style={loadingBox}>Loading profile...</div>
      </div>
    )
  }

  return (
    <div style={pageContainer}>
      <div style={card}>
        <div style={header}>
          <button onClick={() => window.location.href = '/dashboard'} style={backButton}>
            ← Back to Habits
          </button>
          <h2 style={{ margin: 0 }}>👤 Profile</h2>
        </div>

        <div style={avatarSection}>
          <div style={avatar}>
            {name ? name.charAt(0).toUpperCase() : 'U'}
          </div>
          <div style={userInfo}>
            <h3 style={{ margin: '0 0 4px 0' }}>{name || 'User'}</h3>
            <p style={{ margin: 0, color: '#666' }}>{email || 'No email'}</p>
          </div>
        </div>

        {error && <div style={errorBox}>{error}</div>}
        {success && <div style={successBox}>{success}</div>}

        {!editing ? (
          <div style={infoSection}>
            <div style={infoRow}>
              <span style={label}>Name:</span>
              <span style={value}>{name || 'Not set'}</span>
            </div>
            <div style={infoRow}>
              <span style={label}>Email:</span>
              <span style={value}>{email || 'Not set'}</span>
            </div>
            <div style={infoRow}>
              <span style={label}>User ID:</span>
              <span style={value}>{userId}</span>
            </div>
            <div style={buttonGroup}>
              <button onClick={() => setEditing(true)} style={editButton}>
                ✏️ Edit Profile
              </button>
              <button onClick={handleDelete} style={deleteButton}>
                🗑️ Delete Account
              </button>
            </div>
          </div>
        ) : (
          <form onSubmit={handleUpdate} style={formSection}>
            <div style={field}>
              <label style={label}>Name</label>
              <input
                value={name}
                onChange={(e) => setName(e.target.value)}
                style={input}
                required
                minLength={3}
                maxLength={20}
              />
            </div>
            <div style={field}>
              <label style={label}>Email</label>
              <input
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                style={input}
                required
              />
            </div>
            <div style={buttonGroup}>
              <button type="button" onClick={() => setEditing(false)} style={cancelButton}>
                Cancel
              </button>
              <button type="submit" style={saveButton}>
                💾 Save Changes
              </button>
            </div>
          </form>
        )}

        <div style={footer}>
          <LogoutButton />
        </div>
      </div>
    </div>
  )
}

const pageContainer = {
  minHeight: '100vh',
  background: 'linear-gradient(135deg, #f5f7fa 0%, #e4e8ec 100%)',
  padding: '40px 20px',
  fontFamily: '-apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif'
}

const card = {
  maxWidth: 480,
  margin: '0 auto',
  background: '#fff',
  borderRadius: 20,
  boxShadow: '0 10px 40px rgba(0,0,0,.1)',
  overflow: 'hidden'
}

const header = {
  background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
  padding: '24px',
  color: '#fff'
}

const backButton = {
  background: 'rgba(255,255,255,0.2)',
  border: 'none',
  color: '#fff',
  padding: '8px 16px',
  borderRadius: 8,
  cursor: 'pointer',
  marginBottom: 16,
  fontSize: 14,
  fontWeight: 500
}

const loadingBox = {
  textAlign: 'center',
  padding: 60,
  color: '#666'
}

const avatarSection = {
  display: 'flex',
  alignItems: 'center',
  gap: 20,
  padding: '24px',
  borderBottom: '1px solid #eee'
}

const avatar = {
  width: 72,
  height: 72,
  borderRadius: '50%',
  background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
  color: '#fff',
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'center',
  fontSize: 28,
  fontWeight: 600
}

const userInfo = {
  flex: 1
}

const errorBox = {
  margin: '16px 24px 0',
  padding: '12px 16px',
  background: '#ffebee',
  color: '#c62828',
  borderRadius: 8,
  fontSize: 14
}

const successBox = {
  margin: '16px 24px 0',
  padding: '12px 16px',
  background: '#e8f5e9',
  color: '#2e7d32',
  borderRadius: 8,
  fontSize: 14
}

const infoSection = {
  padding: 24
}

const infoRow = {
  display: 'flex',
  justifyContent: 'space-between',
  padding: '12px 0',
  borderBottom: '1px solid #f0f0f0'
}

const label = {
  color: '#888',
  fontWeight: 500
}

const value = {
  color: '#333',
  fontWeight: 500
}

const buttonGroup = {
  display: 'flex',
  gap: 12,
  marginTop: 24,
  flexWrap: 'wrap'
}

const editButton = {
  flex: 1,
  padding: '12px 20px',
  borderRadius: 10,
  border: 'none',
  background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
  color: '#fff',
  cursor: 'pointer',
  fontWeight: 500,
  fontSize: 14
}

const deleteButton = {
  padding: '12px 20px',
  borderRadius: 10,
  border: '1px solid #e57373',
  background: '#fff',
  color: '#e57373',
  cursor: 'pointer',
  fontWeight: 500,
  fontSize: 14
}

const formSection = {
  padding: 24
}

const field = {
  marginBottom: 16
}

const input = {
  width: '100%',
  padding: '12px 16px',
  borderRadius: 10,
  border: '1px solid #ddd',
  fontSize: 14,
  marginTop: 6,
  boxSizing: 'border-box'
}

const cancelButton = {
  flex: 1,
  padding: '12px 20px',
  borderRadius: 10,
  border: '1px solid #ddd',
  background: '#fff',
  color: '#666',
  cursor: 'pointer',
  fontWeight: 500
}

const saveButton = {
  flex: 1,
  padding: '12px 20px',
  borderRadius: 10,
  border: 'none',
  background: 'linear-gradient(135deg, #4caf50 0%, #2e7d32 100%)',
  color: '#fff',
  cursor: 'pointer',
  fontWeight: 500
}

const footer = {
  padding: '16px 24px',
  background: '#f8f9fa',
  display: 'flex',
  justifyContent: 'center'
}
