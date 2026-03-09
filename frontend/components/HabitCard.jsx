import React, { useState } from 'react'
import apiFetch from '../utils/api.js'

export default function HabitCard({ habit, onEdit, onDelete, onRefresh, userId }) {
  const [loadingProgress, setLoadingProgress] = useState(false)
  const [showStats, setShowStats] = useState(false)
  const [stats, setStats] = useState(null)

  const handleMarkComplete = async () => {
    if (!userId) return
    setLoadingProgress(true)
    try {
      const res = await apiFetch(`/progress/${userId}/create/${habit.id}`, { method: 'POST' })
      if (res.ok) {
        alert('Progress marked!')
        if (onRefresh) onRefresh()
      } else {
        alert('Failed to mark progress')
      }
    } catch {
      alert('Error marking progress')
    } finally {
      setLoadingProgress(false)
    }
  }

  const handleShowStats = async () => {
    if (!userId) return
    setShowStats(true)
    try {
      const [streakRes, statRes] = await Promise.all([
        apiFetch(`/progress/${userId}/streak/${habit.id}`),
        apiFetch(`/progress/${userId}/statistic/${habit.id}/week`)
      ])
      const streakData = await streakRes.json()
      const statData = await statRes.json()
      setStats({ streak: streakData?.message, statistic: statData?.message })
    } catch {
      setStats({ streak: 'Error', statistic: 'Error' })
    }
  }

  return (
    <div className="habit-card" style={cardStyle}>
      <div style={badgeStyle(habit.frequency)} className="habit-badge">{habit.frequency}</div>
      <h3 style={{ marginTop: 0, marginBottom: 8 }}>{habit.name}</h3>
      {habit.description && <p style={{ color: '#555', marginTop: 0, marginBottom: 12, fontSize: 14 }}>{habit.description}</p>}
      <div style={rowStyle}>
        <span style={{ fontSize: 12, color: '#888' }}>Created: {habit.createdAt}</span>
      </div>
      <div style={buttonGroupStyle}>
        <button onClick={handleMarkComplete} disabled={loadingProgress} style={buttonPrimary}>
          {loadingProgress ? '...' : '✓ Done Today'}
        </button>
      </div>
      <div style={buttonGroupStyle}>
        <button onClick={handleShowStats} style={buttonOutline}>📊 Stats</button>
        <button onClick={onEdit} style={buttonSecondary}>✏️ Edit</button>
        <button onClick={onDelete} style={buttonDanger}>🗑️ Delete</button>
      </div>

      {showStats && stats && (
        <div style={statsOverlay}>
          <div style={statsModal}>
            <h4 style={{ marginTop: 0 }}>📈 Statistics for "{habit.name}"</h4>
            <div style={statItem}>
              <span style={statLabel}>🔥 Streak:</span>
              <span style={statValue}>{stats.streak || 'N/A'}</span>
            </div>
            <div style={statItem}>
              <span style={statLabel}>📅 This Week:</span>
              <span style={statValue}>{stats.statistic || 'N/A'}</span>
            </div>
            <button onClick={() => setShowStats(false)} style={closeButton}>Close</button>
          </div>
        </div>
      )}
    </div>
  )
}

const cardStyle = {
  background: '#fff',
  borderRadius: 16,
  padding: 20,
  boxShadow: '0 8px 24px rgba(0,0,0,.1)',
  minWidth: 260,
  position: 'relative',
  transition: 'transform 0.2s ease, box-shadow 0.2s ease',
}

function badgeStyle(freq) {
  const color = freq === 'daily' ? '#4caf50' : '#1976d2'
  return { 
    position: 'absolute', 
    top: 16, 
    right: 16, 
    padding: '6px 12px', 
    borderRadius: 20, 
    background: color, 
    color: '#fff', 
    fontSize: 12, 
    textTransform: 'capitalize',
    fontWeight: 500
  }
}

const rowStyle = { display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 12 }
const buttonGroupStyle = { display: 'flex', gap: 8, marginTop: 12, flexWrap: 'wrap' }

const buttonPrimary = { 
  flex: 1,
  padding: '10px 14px', 
  borderRadius: 8, 
  border: 'none', 
  background: 'linear-gradient(135deg, #4caf50, #2e7d32)', 
  color: '#fff', 
  cursor: 'pointer',
  fontWeight: 500,
  transition: 'transform 0.1s'
}

const buttonSecondary = { 
  padding: '8px 12px', 
  borderRadius: 8, 
  border: '1px solid #ddd', 
  background: '#fff', 
  cursor: 'pointer',
  fontSize: 13
}

const buttonOutline = { 
  padding: '8px 12px', 
  borderRadius: 8, 
  border: '1px solid #1976d2', 
  background: '#fff', 
  color: '#1976d2',
  cursor: 'pointer',
  fontSize: 13
}

const buttonDanger = { 
  padding: '8px 12px', 
  borderRadius: 8, 
  border: '1px solid #e57373', 
  background: '#fff', 
  color: '#e57373',
  cursor: 'pointer',
  fontSize: 13
}

const statsOverlay = {
  position: 'fixed',
  left: 0, top: 0, right: 0, bottom: 0,
  background: 'rgba(0,0,0,0.5)',
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'center',
  zIndex: 2000
}

const statsModal = {
  background: '#fff',
  borderRadius: 16,
  padding: 24,
  minWidth: 320,
  maxWidth: 400,
  boxShadow: '0 20px 60px rgba(0,0,0,.3)'
}

const statItem = {
  display: 'flex',
  justifyContent: 'space-between',
  padding: '12px 0',
  borderBottom: '1px solid #eee'
}

const statLabel = { fontWeight: 500, color: '#555' }
const statValue = { fontWeight: 600, color: '#2196f3' }

const closeButton = {
  marginTop: 16,
  width: '100%',
  padding: '10px',
  borderRadius: 8,
  border: 'none',
  background: '#f5f5f5',
  cursor: 'pointer',
  fontWeight: 500
}
