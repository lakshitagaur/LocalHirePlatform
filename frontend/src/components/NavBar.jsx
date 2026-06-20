import { Link } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

function Navbar() {
  const { user, logout } = useAuth()

  return (
    <nav className="bg-cream border-b border-sand">
      <div className="max-w-5xl mx-auto px-6 py-4 flex items-center justify-between">
        <Link to="/" className="font-display text-2xl text-charcoal">
          LocalHire
        </Link>

        <div className="flex items-center gap-6 font-body text-sm">
          <Link to="/" className="text-charcoal/70 hover:text-charcoal">
            Find Work
          </Link>

          {user?.role === 'EMPLOYER' && (
            <Link to="/post-job" className="text-charcoal/70 hover:text-charcoal">
              Post a Job
            </Link>
          )}

          {user ? (
            <div className="flex items-center gap-4">
              <span className="text-charcoal/50">{user.fullName}</span>
              <button
                onClick={logout}
                className="bg-charcoal text-cream px-4 py-2 rounded-md hover:bg-charcoal/90"
              >
                Log out
              </button>
            </div>
          ) : (
            <Link
              to="/login"
              className="bg-clay text-white px-4 py-2 rounded-md hover:bg-clay/90"
            >
              Log in
            </Link>
          )}
        </div>
      </div>
    </nav>
  )
}

export default Navbar