import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { loginUser } from '../api/auth'
import { useAuth } from '../context/AuthContext'

function Login() {
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const { login } = useAuth()
  const navigate = useNavigate()

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    try {
      const response = await loginUser({ email, password })
      login(response.data)
      navigate('/')
    } catch (err) {
      setError(err.response?.data?.error || 'Login failed')
    }
  }

  return (
    <div className="max-w-sm mx-auto px-6 py-16">
      <h1 className="font-display text-2xl text-charcoal mb-6">Log in</h1>

      <form onSubmit={handleSubmit} className="space-y-4">
        <input
          type="email"
          placeholder="Email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          required
          className="w-full px-4 py-2 border border-sand rounded-md font-body text-sm"
        />
        <input
          type="password"
          placeholder="Password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
          className="w-full px-4 py-2 border border-sand rounded-md font-body text-sm"
        />

        {error && <p className="text-red-600 text-sm font-body">{error}</p>}

        <button
          type="submit"
          className="w-full bg-clay text-white py-2.5 rounded-md font-body font-medium hover:bg-clay/90"
        >
          Log in
        </button>
      </form>

      <p className="mt-4 text-sm font-body text-charcoal/60">
        No account? <Link to="/register" className="text-clay">Register here</Link>
      </p>
    </div>
  )
}

export default Login