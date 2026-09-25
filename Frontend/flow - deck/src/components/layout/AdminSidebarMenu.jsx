import { ROLES } from '../../constants/roles'
import { SIDEBAR_MENUS } from '../../constants/sidebarMenus'
import Sidebar from './Sidebar'

function AdminSidebarMenu(props) { return <Sidebar {...props} menuItems={SIDEBAR_MENUS[ROLES.ADMIN]} /> }
export default AdminSidebarMenu
