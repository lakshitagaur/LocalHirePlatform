import { Link } from 'react-router-dom'
import StatusBadge from './StatusBadge'

function JobCard({ job }) {
  return (
    <Link
      to={`/jobs/${job.id}`}
      className="block bg-white border border-sand rounded-lg p-5 shadow-sm hover:shadow-md hover:-translate-y-0.5 transition-all"
    >
      <div className="flex items-start justify-between mb-2">
        <span className="inline-block bg-clay/10 text-clay text-xs font-body font-semibold px-2.5 py-1 rounded-full uppercase tracking-wide">
          {job.category}
        </span>
        <StatusBadge status={job.status} />
      </div>

      <h3 className="font-display text-xl text-charcoal mb-1">{job.title}</h3>
      <p className="font-body text-sm text-charcoal/60 mb-3">{job.locationName}</p>
      <p className="font-body text-sm text-charcoal/80 line-clamp-2">{job.description}</p>

      <div className="mt-4 pt-3 border-t border-sand flex items-center justify-between">
        <span className="font-body text-xs text-charcoal/50">Posted by {job.employerName}</span>
        <span className="font-body text-sm text-clay font-medium">View details →</span>
      </div>
    </Link>
  )
}

export default JobCard