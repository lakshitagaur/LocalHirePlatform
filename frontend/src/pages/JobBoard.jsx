import { useEffect, useState } from 'react'
import { getJobs } from '../api/jobs'
import JobCard from '../components/JobCard'

function JobBoard() {
  const [jobs, setJobs] = useState([])
  const [loading, setLoading] = useState(true)
  const [category, setCategory] = useState('')

  useEffect(() => {
    fetchJobs()
  }, [category])

  const fetchJobs = async () => {
    setLoading(true)
    try {
      const filters = category ? { category } : {}
      const response = await getJobs(filters)
      setJobs(response.data)
    } catch (err) {
      console.error('Failed to fetch jobs', err)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="max-w-5xl mx-auto px-6 py-10">
      <div className="mb-8">
        <h1 className="font-display text-3xl text-charcoal mb-2">Find local work</h1>
        <p className="font-body text-charcoal/60">
          Plumbers, electricians, drivers, cleaners — hired right in your neighborhood.
        </p>
      </div>

      <div className="mb-6">
        <input
          type="text"
          placeholder="Filter by category (e.g. Plumber)"
          value={category}
          onChange={(e) => setCategory(e.target.value)}
          className="w-full max-w-sm px-4 py-2 border border-sand rounded-md font-body text-sm focus:outline-none focus:ring-2 focus:ring-clay/30"
        />
      </div>

      {loading ? (
        <p className="font-body text-charcoal/50">Loading jobs...</p>
      ) : jobs.length === 0 ? (
        <p className="font-body text-charcoal/50">No jobs found. Try a different category.</p>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
          {jobs.map((job) => (
            <JobCard key={job.id} job={job} />
          ))}
        </div>
      )}
    </div>
  )
}

export default JobBoard