import { useEffect, useMemo, useState } from 'react'
import apiFetch from '../utils/api.js'
import HabitCard from './HabitCard'
import HabitForm from './HabitForm'
import styles from '../styles/Habits.module.css'
import LogoutButton from './LogoutButton'

export default function HabitsManager() {
  const [mounted, setMounted] = useState(false)
  const [habits, setHabits] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [showForm, setShowForm] = useState(false)
  const [editHabit, setEditHabit] = useState(null)

  useEffect(() => {
    setMounted(true)
  }, [])

  const userId = mounted ? localStorage.getItem('userId') : null
  const token = mounted ? localStorage.getItem('token') : null

  useEffect(() => {
    if (!mounted) return
    if (!token || !userId) {
      window.location.href = '/'
    }
  }, [mounted, token, userId])

  useEffect(() => {
    if (!mounted) return
    fetchHabits()
  }, [mounted])

  const fetchHabits = async () => {
    if (!userId) return
    try {
      setLoading(true)
      const res = await apiFetch(`/habit/${userId}/all/all`)
      const data = await res.json()
      setHabits(Array.isArray(data) ? data : [])
    } catch (e) {
      setError('Failed to load habits')
    } finally {
      setLoading(false)
    }
  }

  const onCreate = () => {
    setEditHabit(null)
    setShowForm(true)
  }

  const onEdit = (habit) => {
    setEditHabit(habit)
    setShowForm(true)
  }

  const onDelete = async (habitId) => {
    if (!userId) return
    if (!confirm('Delete this habit?')) return
    try {
      await apiFetch(`/habit/${userId}/${habitId}`, { method: 'DELETE' })
      fetchHabits()
    } catch {
      alert('Failed to delete habit')
    }
  }

  const onSubmit = async (payload) => {
    const toSend = {
      name: payload.name,
      description: payload.description && payload.description.trim().length > 0 ? payload.description.trim() : null,
      frequency: payload.frequency,
    }
    try {
      if (editHabit) {
        const res = await apiFetch(`/habit/${userId}/${editHabit.id}`, {
          method: 'PUT',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(toSend),
        })
        if (!res.ok) throw new Error('Update failed')
      } else {
        const res = await apiFetch(`/habit/${userId}/create`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(toSend),
        })
        if (!res.ok) throw new Error('Creation failed')
      }
      setShowForm(false)
      fetchHabits()
    } catch (e) {
      setError('Failed to save habit: ' + (e?.message ?? 'Unknown error'))
    }
  }

  const onCancel = () => setShowForm(false)

  const header = useMemo(() => {
    return editHabit ? `Edit Habit: ${editHabit.name}` : 'Create a new habit'
  }, [editHabit])

  if (!mounted) {
    return (
      <div className={styles.container}>
        <div style={{ textAlign: 'center', padding: 40 }}>Loading...</div>
      </div>
    )
  }

  return (
    <div className={styles.container}>
      <div className={styles.topbar}>
        <h2 style={{ margin: 0, color: '#1a1a2e' }}>Your Habits</h2>
        <div style={{ display: 'flex', gap: 12, alignItems: 'center' }}>
          <button 
            onClick={() => window.location.href = '/profile'}
            style={profileButton}
          >
            👤 Profile
          </button>
          <LogoutButton />
        </div>
      </div>
      <div className={styles.toolbar}>
        <button className={styles.primary} onClick={onCreate}>
          + Create New Habit
        </button>
      </div>
      {loading && <div style={loadingStyle}>Loading habits...</div>}
      {error && <div className={styles.error}>{error}</div>}
      <div className={styles.grid}>
        {habits.map(h => (
          <HabitCard 
            key={h.id} 
            habit={h} 
            onEdit={() => onEdit(h)} 
            onDelete={() => onDelete(h.id)}
            onRefresh={fetchHabits}
            userId={userId}
          />
        ))}
      </div>
      {habits.length === 0 && !loading && (
        <div style={emptyState}>
          <p>No habits yet. Create your first habit to get started!</p>
        </div>
      )}
      {showForm && (
        <div className={styles.modalOverlay}>
          <div className={styles.modalContent}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16 }}>
              <h3 style={{ margin: 0 }}>{header}</h3>
              <button onClick={onCancel} style={closeIcon}>×</button>
            </div>
            <HabitForm initialData={editHabit || { name: '', description: '', frequency: 'daily' }} onSubmit={onSubmit} onCancel={onCancel} />
          </div>
        </div>
      )}
    </div>
  )
}

const profileButton = {
  padding: '8px 16px',
  borderRadius: 8,
  border: '1px solid #1976d2',
  background: '#fff',
  color: '#1976d2',
  cursor: 'pointer',
  fontWeight: 500,
  transition: 'all 0.2s'
}

const loadingStyle = {
  textAlign: 'center',
  padding: 40,
  color: '#666',
  fontSize: 16
}

const emptyState = {
  textAlign: 'center',
  padding: 60,
  color: '#888',
  background: '#f8f9fa',
  borderRadius: 12,
  marginTop: 20
}

const closeIcon = {
  background: 'none',
  border: 'none',
  fontSize: 28,
  cursor: 'pointer',
  color: '#888',
  lineHeight: 1
}
