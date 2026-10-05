import { Link } from 'react-router-dom'
import PlaceholderPage from '../components/common/PlaceholderPage'
export default function NotFoundPage() { return <><PlaceholderPage title="Page not found" description="The requested page does not exist." /><Link to="/login">Return to sign in</Link></> }
