import { useState, useEffect } from 'react'

export default function ResetPassword() {
  const [mounted, setMounted] = useState(false)
  const [email, setEmail] = useState('')
  const [step, setStep] = useState(1)
  const [token, setToken] = useState('')
  const [newPassword, setNewPassword] = useState('')
  const [error, setError] = useState(null)
  const [success, setSuccess] = useState(null)
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    setMounted(true)
  }, [])

  const apiBase = mounted 
    ? (window.__API_BASE__ || process.env.NEXT_PUBLIC_API_BASE_URL || 'http://localhost:8080')
    : 'http://localhost:8080'

  const handleRequestReset = async (e) => {
    e.preventDefault()
    setError(null)
    setLoading(true)
    try {
      const res = await fetch(`${apiBase}/login/requestReset/${email}`, { method: 'POST' })
      if (res.ok) {
        setSuccess('Reset link sent to your email!')
        setStep(2)
      } else {
        setError('Failed to send reset email')
      }
    } catch {
      setError('Connection error')
    } finally {
      setLoading(false)
    }
  }

  const handleReset = async (e) => {
    e.preventDefault()
    setError(null)
    setLoading(true)
    try {
      const res = await fetch(`${apiBase}/login/reset`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, token, newPassword })
      })
      const data = await res.json()
      if (res.ok && data?.id != null) {
        setSuccess('Password reset successful! Redirecting to login...')
        setTimeout(() => window.location.href = '/', 2000)
      } else {
        setError('Invalid token or email')
      }
    } catch {
      setError('Connection error')
    } finally {
      setLoading(false)
    }
  }

  if (!mounted) {
    return (
      <div style={pageContainer}>
        <div style={card}>
          <div style={{ textAlign: 'center', padding: 40 }}>Loading...</div>
        </div>
      </div>
    )
  }

  return (
    <div style={pageContainer}>
      <div style={card}>
        <button onClick={() => window.location.href = '/'} style={backButton}>
          ← Back to Login
        </button>

        <div style={logoSection}>
          <div style={logo}>🔐</div>
          <h1 style={title}>{step === 1 ? 'Reset Password' : 'Enter New Password'}</h1>
          <p style={subtitle}>{step === 1 ? 'Enter your email to receive a reset link' : 'Enter the token from your email'}</p>
        </div>

        {error && <div style={errorBox}>{error}</div>}
        {success && <div style={successBox}>{success}</div>}

        {step === 1 ? (
          <form onSubmit={handleRequestReset} style={form}>
            <div style={field}>
              <label style={label}>Email</label>
              <input 
                type="email"
                placeholder="Enter your email" 
                value={email} 
                onChange={e => setEmail(e.target.value)} 
                style={input}
                required 
              />
            </div>
            <button type="submit" disabled={loading} style={submitButton}>
              {loading ? 'Sending...' : 'Send Reset Link'}
            </button>
          </form>
        ) : (
          <form onSubmit={handleReset} style={form}>
            <div style={field}>
              <label style={label}>Reset Token</label>
              <input 
                placeholder="Enter token from email" 
                value={token} 
                onChange={e => setToken(e.target.value)} 
                style={input}
                required 
              />
            </div>
            <div style={field}>
              <label style={label}>New Password</label>
              <input 
                type="password"
                placeholder="Enter new password" 
                value={newPassword} 
                onChange={e => setNewPassword(e.target.value)} 
                style={input}
                required 
                minLength={6}
              />
            </div>
            <button type="submit" disabled={loading} style={submitButton}>
              {loading ? 'Resetting...' : 'Reset Password'}
            </button>
          </form>
        )}
      </div>
    </div>
  )
}

const pageContainer = {
  minHeight: '100vh',
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'center',
  background: 'linear-gradient(135deg, #11998e 0%, #38ef7d 100%)',
  padding: 20,
  fontFamily: '-apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif'
}

const card = {
  width: '100%',
  maxWidth: 400,
  background: '#fff',
  borderRadius: 24,
  padding: 40,
  boxShadow: '0 20px 60px rgba(0,0,0,.2)'
}

const backButton = {
  background: 'none',
  border: 'none',
  color: '#666',
  cursor: 'pointer',
  fontSize: 14,
  marginBottom: 20,
  padding: 0
}

const logoSection = {
  textAlign: 'center',
  marginBottom: 32
}

const logo = {
  width: 64,
  height: 64,
  borderRadius: '50%',
  background: 'linear-gradient(135deg, #11998e 0%, #38ef7d 100%)',
  color: '#fff',
  fontSize: 28,
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'center',
  margin: '0 auto 16px'
}

const title = {
  margin: '0 0 8px 0',
  fontSize: 24,
  color: '#1a1a2e',
  fontWeight: 700
}

const subtitle = {
  margin: 0,
  color: '#666',
  fontSize: 14
}

const form = {
  display: 'flex',
  flexDirection: 'column',
  gap: 20
}

const field = {
  display: 'flex',
  flexDirection: 'column',
  gap: 6
}

const label = {
  fontSize: 14,
  fontWeight: 500,
  color: '#333'
}

const input = {
  padding: '14px 16px',
  borderRadius: 12,
  border: '1px solid #ddd',
  fontSize: 15,
  outline: 'none'
}

const errorBox = {
  padding: '12px 16px',
  background: '#ffebee',
  color: '#c62828',
  borderRadius: 10,
  fontSize: 14,
  marginBottom: 20,
  textAlign: 'center'
}

const successBox = {
  padding: '12px 16px',
  background: '#e8f5e9',
  color: '#2e7d32',
  borderRadius: 10,
  fontSize: 14,
  marginBottom: 20,
  textAlign: 'center'
}

const submitButton = {
  padding: '16px',
  borderRadius: 12,
  border: 'none',
  background: 'linear-gradient(135deg, #11998e 0%, #38ef7d 100%)',
  color: '#fff',
  fontSize: 16,
  fontWeight: 600,
  cursor: 'pointer',
  marginTop: 8
}
