import { ROLES } from '../../constants/roles'
import { SIDEBAR_MENUS } from '../../constants/sidebarMenus'
import Sidebar from './Sidebar'

function EmployeeSidebarMenu(props) { return <Sidebar {...props} menuItems={SIDEBAR_MENUS[ROLES.EMPLOYEE]} /> }
export default EmployeeSidebarMenu
