import { Link } from 'react-router-dom'
import PlaceholderPage from '../components/common/PlaceholderPage'
export default function UnauthorizedPage() { return <><PlaceholderPage title="Access denied" description="You do not have permission to view this page." /><Link to="/login">Return to sign in</Link></> }
