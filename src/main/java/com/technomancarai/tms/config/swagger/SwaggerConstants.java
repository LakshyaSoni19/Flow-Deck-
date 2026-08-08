package com.technomancarai.tms.config.swagger;

/**
 * Constant values for Swagger / OpenAPI documentation configuration.
 */
public final class SwaggerConstants {

    private SwaggerConstants() {
        // Private constructor to prevent instantiation
    }

    // General API Info
    public static final String API_TITLE = "Flow Deck API";
    public static final String API_DESCRIPTION = "Flow Deck – Enterprise Task Management System REST APIs with Role-Based Access Control (RBAC)";
    public static final String API_VERSION = "v1.0.0";

    // Contact Information
    public static final String CONTACT_NAME = "Flow Deck Support";
    public static final String CONTACT_EMAIL = "support@flowdeck.com";

    // License Information
    public static final String LICENSE_NAME = "Apache 2.0";
    public static final String LICENSE_URL = "https://www.apache.org/licenses/LICENSE-2.0";

    // Security Scheme Constants
    public static final String SECURITY_SCHEME_NAME = "bearerAuth";
    public static final String SECURITY_SCHEME = "bearer";
    public static final String BEARER_FORMAT = "JWT";

    // Group & Package Scanning Configuration
    public static final String PUBLIC_API_GROUP = "public-apis";
    public static final String CONTROLLER_PACKAGE = "com.technomancarai.tms.controller";

    // Tag Names
    public static final String TAG_01_AUTH = "01. Authentication";
    public static final String TAG_02_ADMIN_APPROVAL = "02. Admin User Approval";
    public static final String TAG_03_USER_MGMT = "03. User Management";
    public static final String TAG_04_ROLE_MGMT = "04. Role Management";
    public static final String TAG_05_DEPT_MGMT = "05. Department Management";
    public static final String TAG_06_DESIG_MGMT = "06. Designation Management";
    public static final String TAG_07_PROJECT_MGMT = "07. Project Management";
    public static final String TAG_08_PM_WORKSPACE = "08. Project Manager Workspace";
    public static final String TAG_09_EMPLOYEE_WORKSPACE = "09. Employee Workspace";
    public static final String TAG_10_DASHBOARD = "10. Dashboard";
    public static final String TAG_11_NOTIFICATIONS = "11. Notifications";
    public static final String TAG_12_REPORTS = "12. Reports";

    // Tag Descriptions
    public static final String TAG_01_AUTH_DESC = "User registration, login, OTP verification, and password management endpoints";
    public static final String TAG_02_ADMIN_APPROVAL_DESC = "Endpoints for Admin to review, approve, and reject user registration requests";
    public static final String TAG_03_USER_MGMT_DESC = "Endpoints for managing users, activating/deactivating, and soft deletion";
    public static final String TAG_04_ROLE_MGMT_DESC = "Endpoints for creating, updating, listing roles, and assigning roles to users";
    public static final String TAG_05_DEPT_MGMT_DESC = "Endpoints for department organization and structure management";
    public static final String TAG_06_DESIG_MGMT_DESC = "Endpoints for employee designation and job title management";
    public static final String TAG_07_PROJECT_MGMT_DESC = "Endpoints for managing projects, status updates, and manager assignment";
    public static final String TAG_08_PM_WORKSPACE_DESC = "Endpoints for Project Managers to manage assigned projects, project members, tasks, and progress statistics";
    public static final String TAG_09_EMPLOYEE_WORKSPACE_DESC = "Endpoints for Employees to view assigned projects/tasks, manage comments, update task status, view personal dashboard, and manage profile";
    public static final String TAG_10_DASHBOARD_DESC = "Endpoints for executive, manager, and employee dashboard metrics and analytics";
    public static final String TAG_11_NOTIFICATIONS_DESC = "Endpoints for system notification dispatch and alert services";
    public static final String TAG_12_REPORTS_DESC = "Endpoints for system reporting and activity audit analytics";
}
