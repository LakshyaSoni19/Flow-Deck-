import { ROLES } from '../../constants/roles'
import { SIDEBAR_MENUS } from '../../constants/sidebarMenus'
import Sidebar from './Sidebar'

function ProjectManagerSidebarMenu(props) { return <Sidebar {...props} menuItems={SIDEBAR_MENUS[ROLES.PROJECT_MANAGER]} /> }
export default ProjectManagerSidebarMenu
