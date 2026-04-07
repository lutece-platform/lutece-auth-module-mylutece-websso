/*
 * Copyright (c) 2002-2014, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.mylutece.modules.wssodatabase.authentication.web;

import fr.paris.lutece.plugins.mylutece.business.attribute.AttributeHome;
import fr.paris.lutece.plugins.mylutece.business.attribute.IAttribute;
import fr.paris.lutece.plugins.mylutece.modules.wssodatabase.authentication.business.IdxWSSODatabaseHome;
import fr.paris.lutece.plugins.mylutece.modules.wssodatabase.authentication.business.WssoProfil;
import fr.paris.lutece.plugins.mylutece.modules.wssodatabase.authentication.business.WssoProfilHome;
import fr.paris.lutece.plugins.mylutece.modules.wssodatabase.authentication.business.WssoUser;
import fr.paris.lutece.plugins.mylutece.modules.wssodatabase.authentication.business.WssoUserHome;
import fr.paris.lutece.plugins.mylutece.modules.wssodatabase.authentication.business.WssoUserRoleHome;
import fr.paris.lutece.plugins.mylutece.modules.wssodatabase.authentication.business.WssoUserWrapper;
import fr.paris.lutece.plugins.mylutece.modules.wssodatabase.authentication.service.ImportWssoDatabaseUserService;
import fr.paris.lutece.plugins.mylutece.modules.wssodatabase.authentication.service.WssoDatabaseService;
import fr.paris.lutece.plugins.mylutece.modules.wssodatabase.authentication.util.LdapBrowser;
import fr.paris.lutece.plugins.mylutece.service.MyLutecePlugin;
import fr.paris.lutece.plugins.mylutece.service.RoleResourceIdService;
import fr.paris.lutece.portal.business.role.Role;
import fr.paris.lutece.portal.business.role.RoleHome;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.admin.AdminUserService;
import fr.paris.lutece.portal.service.csv.CSVMessageDescriptor;
import fr.paris.lutece.portal.service.fileupload.FileUploadService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.mail.MailService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.web.admin.PluginAdminPageJspBean;
import fr.paris.lutece.portal.web.constants.Messages;
import fr.paris.lutece.portal.web.constants.Parameters;
import fr.paris.lutece.portal.web.pluginaction.DefaultPluginActionResult;
import fr.paris.lutece.portal.web.upload.MultipartHttpServletRequest;
import fr.paris.lutece.portal.web.util.LocalizedPaginator;
import fr.paris.lutece.util.ReferenceItem;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.filesystem.FileSystemUtil;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.html.ItemNavigator;
import fr.paris.lutece.util.html.Paginator;
import fr.paris.lutece.util.sort.AttributeComparator;
import fr.paris.lutece.util.url.UrlItem;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang3.StringUtils;


/**
 * This class provides the user interface to manage roles features ( manage,
 * create, modify, remove )
 */
public class WssodatabaseJspBean extends PluginAdminPageJspBean
{
    // Right
    public static final String RIGHT_MANAGE_WSSO_USERS = "WSSODATABASE_MANAGEMENT_USERS";
    public static final String RIGHT_MANAGE_WSSO_ROLES = "CORE_ROLES_MANAGEMENT";
    public static final String RIGHT_MANAGE_WSSO_PROFILS = "WSSODATABASE_MANAGEMENT_PROFILS";
    public static final String RESOURCE_TYPE = "WSSO_DATABASE";
    public static final String PERMISSION_IMPORT_EXPORT_WSSO_DATABASE_USERS = "IMPORT_EXPORT_WSSO_DATABASE_USERS";
    public static final String WSSODATABASE_MANAGEMENT_USERS = "WSSODATABASE_MANAGEMENT_USERS";

    /**
     *
     */
    private static final long serialVersionUID = 1389282258444625177L;

    /////////////////////////////////////////////////////////////////////////////////////////
    // Constants
    private static final String SPACE = " ";
    private static final String CONSTANT_EXTENSION_CSV_FILE = ".csv";
    private static final String CONSTANT_EXTENSION_XML_FILE = ".xml";
    private static final String CONSTANT_MIME_TYPE_CSV = "application/csv";
    private static final String CONSTANT_MIME_TYPE_XML = "application/xml";
    private static final String CONSTANT_MIME_TYPE_TEXT_CSV = "text/csv";
    private static final String CONSTANT_MIME_TYPE_OCTETSTREAM = "application/octet-stream";
    private static final String CONSTANT_EXPORT_USERS_FILE_NAME = "users";
    private static final String CONSTANT_POINT = ".";
    private static final String CONSTANT_QUOTE = "\"";
    private static final String CONSTANT_ATTACHEMENT_FILE_NAME = "attachement; filename=\"";
    private static final String CONSTANT_ATTACHEMENT_DISPOSITION = "Content-Disposition";
    private static final String MARK_EXPORT_USERS = "users";

    //JSP
    private static final String MANAGE_USERS = "ManageUsers.jsp";
    private static final String JSP_DO_REMOVE_USER = "jsp/admin/plugins/mylutece/modules/wssodatabase/DoRemoveUser.jsp";
    private static final String JSP_DO_REMOVE_PROFIL = "jsp/admin/plugins/mylutece/modules/wssodatabase/DoRemoveProfil.jsp";
    private static final String MANAGE_PROFILS = "ManageProfils.jsp";
    private static final String MANAGE_ROLES_PROFIL = "ManageRolesProfil.jsp";
    private static final String JSP_URL_MANAGE_USERS_PROFIL = "jsp/admin/plugins/mylutece/modules/database/ManageUsersProfil.jsp";
    private static final String JSP_DO_REMOVE_ASIGN_USER_PROFIL = "jsp/admin/plugins/mylutece/modules/wssodatabase/DoRemoveAssignUserProfil.jsp";
    private static final String MANAGE_PROFILS_USER = "ManageProfilsUser.jsp";

    //Propety
    private static final String PROPERTY_PAGE_TITLE_MANAGE_USERS = "module.mylutece.wssodatabase.manage_users.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MANAGE_ROLES_USER = "module.mylutece.wssodatabase.manage_roles_user.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_CREATE_USER = "module.mylutece.wssodatabase.create_user.pageTitle";

    //    private static final String PROPERTY_USERS_PER_PAGE = "paginator.users.itemsPerPage";
    private static final String PROPERTY_PROFILS_PER_PAGE = "paginator.profils.itemsPerPage";
    private static final String PROPERTY_PAGE_TITLE_MANAGE_PROFILS = "module.mylutece.wssodatabase.manage_profils.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MANAGE_ROLES_PROFIL = "module.mylutece.wssodatabase.manage_roles_profil.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MANAGE_USERS_PROFIL = "module.mylutece.wssodatabase.manage_users_profil.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_CREATE_PROFIL = "module.mylutece.wssodatabase.create_profil.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MANAGE_PROFILS_USER = "module.mylutece.wssodatabase.manage_profils_user.pageTitle";
    private static final String PROPERTY_IMPORT_USERS_FROM_FILE_PAGETITLE = "module.mylutece.wssodatabase.import_users_from_file.pageTitle";
    private static final String PROPERTY_NO_REPLY_EMAIL = "mail.noreply.email";
    private static final String PROPERTY_SITE_NAME = "lutece.name";

    //Messages
    private static final String MESSAGE_CONFIRM_REMOVE_USER = "module.mylutece.wssodatabase.message.confirmRemoveUser";
    private static final String MESSAGE_USER_EXIST = "module.mylutece.wssodatabase.message.user_exist";
    private static final String MESSAGE_ERROR_CREATE_USER = "module.mylutece.wssodatabase.message.create.user";
    private static final String MESSAGE_ERROR_REMOVE_USER = "module.mylutece.wssodatabase.message.remove.user";
    private static final String MESSAGE_CONFIRM_REMOVE_PROFIL = "module.mylutece.wssodatabase.message.confirmRemoveProfil";
    private static final String MESSAGE_CONFIRM_REMOVE_ASSIGN_PROFIL_USER = "module.mylutece.wssodatabase.message.confirmAssignProfilUser";
    private static final String MESSAGE_ERROR_REMOVE_PROFIL = "module.mylutece.wssodatabase.message.remove.profil";
    private static final String MESSAGE_ERROR_REMOVE_ASSIGNED_PROFIL = "module.mylutece.wssodatabase.message.remove.assigned_profil";
    private static final String MESSAGE_PROFIL_EXIST = "module.mylutece.wssodatabase.message.profil_exist";
    private static final String MESSAGE_MANDATORY_FIELD = "portal.util.message.mandatoryField";
    private static final String MESSAGE_ERROR_CSV_FILE_IMPORT = "module.mylutece.wssodatabase.import_users_from_file.error_csv_file_import";
    private static final String MESSAGE_ACCOUNT_IMPORTED_MAIL_SUBJECT = "module.mylutece.wssodatabase.import_users_from_file.email.mailSubject";

    // Parameters
    private static final String PARAMETER_PLUGIN_NAME = "plugin_name";
    private static final String PARAMETER_MYLUTECE_WSSO_ROLE_ID = "mylutece_wsso_role_id";
    private static final String PARAMETER_MYLUTECE_WSSO_USER_ID = "mylutece_wsso_user_id";
    private static final String PARAMETER_GUID = "guid";
    private static final String PARAMETER_LAST_NAME = "last_name";
    private static final String PARAMETER_FIRST_NAME = "first_name";
    private static final String PARAMETER_EMAIL = "email";
    private static final String PARAMETER_MYLUTECE_WSSO_PROFIL_CODE = "mylutece_wsso_profil_code";
    private static final String PARAMETER_CODE = "code";
    private static final String PARAMETER_DESCRIPTION = "description";
    private static final String PARAMETER_MYLUTECE_DATABASE_ROLE_IDS = "mylutece_database_role_id";
    private static final String PARAMETER_AVAILABLE_USERS = "available_users";
    private static final String MARK_AND_PARAMETER_SEARCH_CODE = "search_code";
    private static final String MARK_AND_PARAMETER_SEARCH_DESCRIPTION = "search_description";
    private static final String MARK_AND_PARAMETER_SEARCH_LASTNAME = "search_last_name";
    private static final String MARK_AND_PARAMETER_SEARCH_FIRSTNAME = "search_first_name";
    private static final String MARK_AND_PARAMETER_SEARCH_EMAIL = "search_email";
    private static final String MARK_AND_PARAMETER_ASSIGNED_USER_ID = "assigned_user_id";
    private static final String PARAMETER_XSL_EXPORT_ID = "xsl_export_id";
    private static final String PARAMETER_EXPORT_PROFILS = "export_profils";
    private static final String PARAMETER_EXPORT_ROLES = "export_roles";
    private static final String PARAMETER_EXPORT_GROUPS = "export_groups";
    private static final String PARAMETER_IMPORT_USERS_FILE = "import_file";
    private static final String PARAMETER_SKIP_FIRST_LINE = "ignore_first_line";
    private static final String PARAMETER_UPDATE_USERS = "update_existing_users";
    private static final String MARK_SITE_NAME = "site_name";
    private static final String MARK_SITE_LINK = "site_link";

    // Marks FreeMarker
    private static final String MARK_ROLES_LIST = "role_list";
    private static final String MARK_ROLES_LIST_FOR_USER = "user_role_list";
    private static final String MARK_USERS_LIST = "user_list";
    private static final String MARK_USER = "user";
    private static final String MARK_PLUGIN_NAME = "plugin_name";
    private static final String MARK_LAST_NAME = "last_name";
    private static final String MARK_FIRST_NAME = "first_name";
    private static final String MARK_EMAIL = "email";
    private static final String MARK_PROFILS_LIST = "profil_list";
    private static final String MARK_PAGINATOR = "paginator";
    private static final String MARK_NB_ITEMS_PER_PAGE = "nb_items_per_page";
    private static final String MARK_ITEM_NAVIGATOR = "item_navigator";
    private static final String MARK_PROFIL = "profil";
    private static final String MARK_LOCALE = "locale";
    private static final String MARK_AVAILABLE_USERS = "available_users";
    private static final String MARK_ASSIGNED_USERS = "assigned_users";
    private static final String MARK_ASSIGNED_USERS_NUMBER = "assigned_users_number";
    private static final String MARK_LIST_XSL_EXPORT = "refListXsl";
    private static final String MARK_LIST_MESSAGES = "messages";
    private static final String MARK_CSV_SEPARATOR = "csv_separator";
    private static final String MARK_CSV_ESCAPE = "csv_escape";
    private static final String MARK_ATTRIBUTES_SEPARATOR = "attributes_separator";
    private static final String MARK_PROFILS_LIST_FOR_USER = "profil_user_list";
    private static final String MARK_RIGHT_MANAGE_ROLES = "manageRoles";
    private static final String ATTRIBUTE_IMPORT_USERS_LIST_MESSAGES = "importUsersListMessages";

    // Templates
    private static final String TEMPLATE_CREATE_USER = "admin/plugins/mylutece/modules/wssodatabase/create_user.html";
    private static final String TEMPLATE_MANAGE_USERS = "admin/plugins/mylutece/modules/wssodatabase/manage_users.html";
    private static final String TEMPLATE_MANAGE_ROLES_USER = "admin/plugins/mylutece/modules/wssodatabase/manage_roles_user.html";
    private static final String TEMPLATE_MANAGE_PROFILS = "admin/plugins/mylutece/modules/wssodatabase/manage_profils.html";
    private static final String TEMPLATE_CREATE_PROFIL = "admin/plugins/mylutece/modules/wssodatabase/create_profil.html";
    private static final String TEMPLATE_MODIFY_PROFIL = "admin/plugins/mylutece/modules/wssodatabase/modify_profil.html";
    private static final String TEMPLATE_MANAGE_ROLES_PROFIL = "admin/plugins/mylutece/modules/wssodatabase/manage_roles_profil.html";
    private static final String TEMPLATE_MANAGE_USERS_PROFIL = "admin/plugins/mylutece/modules/wssodatabase/manage_users_profil.html";
    private static final String TEMPLATE_EXPORT_USERS_FROM_FILE = "admin/plugins/mylutece/modules/wssodatabase/export_users.html";
    private static final String TEMPLATE_IMPORT_USERS_FROM_FILE = "admin/plugins/mylutece/modules/wssodatabase/import_users_from_file.html";
    private static final String TEMPLATE_MANAGE_PROFILS_USER = "admin/plugins/mylutece/modules/wssodatabase/manage_profils_user.html";
    private static final String TEMPLATE_MAIL_USER_IMPORTED = "admin/plugins/mylutece/modules/wssodatabase/mail_user_imported.html";
    public static final String TEMPLATE_EXPORT_USERS = "admin/plugins/mylutece/modules/wssodatabase/export/export_csv.ftl";

    private static final String FIELD_IMPORT_USERS_FILE = "module.mylutece.wssodatabase.import_users_from_file.labelImportFile";
    private static final String CONSTANT_WILDCARD = "*";

    //    private Plugin _plugin;

    // Variables
    private int _nItemsPerPage;
    private int _nDefaultItemsPerPage;
    private String _strCurrentPageIndex;
    private String _strSortedAttributeName;
    private boolean _bIsAscSort = true;
    private ItemNavigator _itemNavigator;
    private WssoDatabaseService _databaseService = WssoDatabaseService.getInstance( );
    private ImportWssoDatabaseUserService _importWssoDatabaseUserService = new ImportWssoDatabaseUserService( );

    /**
     * Creates a new WssodatabaseJspBean object.
     */
    public WssodatabaseJspBean( )
    {
    }

    /**
     * Returns the User creation form
     * 
     * @param request The Http request
     * @return Html creation form
     */
    public String getCreateUser( HttpServletRequest request )
    {
        LdapBrowser ldap = new LdapBrowser( );
        Collection userList = null;

        setPageTitleProperty( PROPERTY_PAGE_TITLE_CREATE_USER );

        // get the filter parameters
        String strUserLastName = request.getParameter( PARAMETER_LAST_NAME );

        if ( strUserLastName == null )
        {
            strUserLastName = "";
        }

        String strUserFirstName = request.getParameter( PARAMETER_FIRST_NAME );

        if ( strUserFirstName == null )
        {
            strUserFirstName = "";
        }

        String strUserEMail = request.getParameter( PARAMETER_EMAIL );

        if ( strUserEMail == null )
        {
            strUserEMail = "";
        }

        //search in LDAP
        if ( !( ( strUserLastName.equals( "" ) ) && ( strUserFirstName.equals( "" ) ) && ( strUserEMail.equals( "" ) ) ) )
        {
            try
            {
                userList = ldap.getUserList( strUserLastName + CONSTANT_WILDCARD, strUserFirstName + CONSTANT_WILDCARD,
                        strUserEMail + CONSTANT_WILDCARD );
            }
            catch ( Exception e )
            {
                AppLogService.error( e.getMessage( ), e );
            }
        }

        Map<String, Object> model = new HashMap<String, Object>( );
        model.put( MARK_USERS_LIST, userList );
        model.put( MARK_PLUGIN_NAME, getPlugin( ).getName( ) );
        model.put( MARK_LAST_NAME, strUserLastName );
        model.put( MARK_FIRST_NAME, strUserFirstName );
        model.put( MARK_EMAIL, strUserEMail );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_CREATE_USER, getLocale( ), model );

        return getAdminPage( template.getHtml( ) );
    }

    /**
     * Returns the Profil creation form
     * 
     * @param request The Http request
     * @return Html creation form
     */
    public String getCreateProfil( HttpServletRequest request )
    {
        setPageTitleProperty( PROPERTY_PAGE_TITLE_CREATE_PROFIL );

        HashMap<String, Object> model = new HashMap<String, Object>( );
        model.put( MARK_PLUGIN_NAME, getPlugin( ).getName( ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_CREATE_PROFIL, getLocale( ), model );

        return getAdminPage( template.getHtml( ) );
    }

    /**
     * Process user's creation
     * 
     * @param request The Http request
     * @return The user's Displaying Url
     */
    public String doCreateUser( HttpServletRequest request )
    {
        WssoUser user = null;
        LdapBrowser ldap = new LdapBrowser( );

        String strUserGuid = request.getParameter( PARAMETER_GUID );

        if ( strUserGuid == null )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_ERROR_CREATE_USER, AdminMessage.TYPE_ERROR );
        }

        Collection userList = WssoUserHome.findWssoUsersListForGuid( strUserGuid, getPlugin( ) );

        if ( userList.size( ) == 0 )
        {
            user = ldap.getUserPublicData( strUserGuid );

            if ( user == null )
            {
                return AdminMessageService.getMessageUrl( request, MESSAGE_ERROR_CREATE_USER, AdminMessage.TYPE_ERROR );
            }

            WssoUserHome.create( user, getPlugin( ) );
            notifyUserAccountCreated( user, AdminUserService.getLocale( request ), AppPathService.getBaseUrl( request ) );
        }
        else
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_USER_EXIST, AdminMessage.TYPE_STOP );
        }

        return MANAGE_USERS + "?" + PARAMETER_PLUGIN_NAME + "=" + getPlugin( ).getName( );
    }

    /**
     * Process profil's creation
     * 
     * @param request The Http request
     * @return The user's Displaying Url
     */
    public String doCreateProfil( HttpServletRequest request )
    {
        String strCode = request.getParameter( PARAMETER_CODE );
        String strDescription = request.getParameter( PARAMETER_DESCRIPTION );

        if ( StringUtils.isBlank( strCode ) || StringUtils.isBlank( strDescription ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }

        WssoProfil wssoProfil = WssoProfilHome.findWssoProfilByCode( strCode, getPlugin( ) );

        if ( wssoProfil != null )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_PROFIL_EXIST, AdminMessage.TYPE_STOP );
        }

        wssoProfil = new WssoProfil( );
        wssoProfil.setCode( strCode );
        wssoProfil.setDescription( strDescription );

        WssoProfilHome.create( wssoProfil, getPlugin( ) );

        return MANAGE_PROFILS + "?" + PARAMETER_PLUGIN_NAME + "=" + getPlugin( ).getName( );
    }

    /**
     * Process profil's modification
     * 
     * @param request The Http request
     * @return The user's Displaying Url
     */
    public String doModifyProfil( HttpServletRequest request )
    {
        String mlWssoProfilCode = request.getParameter( PARAMETER_MYLUTECE_WSSO_PROFIL_CODE );

        if ( mlWssoProfilCode == null )
        {
            return getManageProfils( request );
        }

        WssoProfil wssoProfil = WssoProfilHome.findWssoProfilByCode( mlWssoProfilCode, getPlugin( ) );

        if ( wssoProfil == null )
        {
            getManageProfils( request );
        }

        String strDescription = request.getParameter( PARAMETER_DESCRIPTION );

        if ( StringUtils.isBlank( strDescription ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }

        wssoProfil.setDescription( strDescription );

        WssoProfilHome.update( wssoProfil, getPlugin( ) );

        return MANAGE_PROFILS + "?" + PARAMETER_PLUGIN_NAME + "=" + getPlugin( ).getName( );
    }

    /**
     * Returns removal user's form
     * 
     * @param request The Http request
     * @return Html form
     */
    public String getRemoveUser( HttpServletRequest request )
    {
        int nUserId = Integer.parseInt( request.getParameter( PARAMETER_MYLUTECE_WSSO_USER_ID ) );

        UrlItem url = new UrlItem( JSP_DO_REMOVE_USER );
        url.addParameter( PARAMETER_PLUGIN_NAME, getPlugin( ).getName( ) );
        url.addParameter( PARAMETER_MYLUTECE_WSSO_USER_ID, nUserId );

        return AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_USER, url.getUrl( ),
                AdminMessage.TYPE_CONFIRMATION );
    }

    /**
     * Returns removal profil's form
     * 
     * @param request The Http request
     * @return Html form
     */
    public String getRemoveProfil( HttpServletRequest request )
    {
        String mlWssoProfilCode = request.getParameter( PARAMETER_MYLUTECE_WSSO_PROFIL_CODE );

        if ( mlWssoProfilCode == null )
        {
            return getManageProfils( request );
        }

        UrlItem url = new UrlItem( JSP_DO_REMOVE_PROFIL );
        url.addParameter( PARAMETER_PLUGIN_NAME, getPlugin( ).getName( ) );
        url.addParameter( PARAMETER_MYLUTECE_WSSO_PROFIL_CODE, mlWssoProfilCode );

        boolean isAssigned = WssoProfilHome.checkProfilAssigned( mlWssoProfilCode, getPlugin( ) );

        if ( isAssigned )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_ERROR_REMOVE_ASSIGNED_PROFIL,
                    AdminMessage.TYPE_STOP );
        }

        return AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_PROFIL, url.getUrl( ),
                AdminMessage.TYPE_CONFIRMATION );
    }

    /**
     * Process user's removal
     * 
     * @param request The Http request
     * @return The Jsp management URL of the process result
     */
    public String doRemoveUser( HttpServletRequest request )
    {
        if ( request.getParameter( PARAMETER_MYLUTECE_WSSO_USER_ID ) == null )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_ERROR_REMOVE_USER, AdminMessage.TYPE_ERROR );
        }

        int nUserId = Integer.parseInt( request.getParameter( PARAMETER_MYLUTECE_WSSO_USER_ID ) );

        WssoUser user = WssoUserHome.findByPrimaryKey( nUserId, getPlugin( ) );

        if ( user == null )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_ERROR_REMOVE_USER, AdminMessage.TYPE_ERROR );
        }

        WssoUserHome.remove( user, getPlugin( ) );
        WssoUserRoleHome.deleteRolesForUser( user.getMyluteceWssoUserId( ), getPlugin( ) );

        return MANAGE_USERS + "?" + PARAMETER_PLUGIN_NAME + "=" + getPlugin( ).getName( );
    }

    /**
     * Process profil's removal
     * 
     * @param request The Http request
     * @return The Jsp management URL of the process result
     */
    public String doRemoveProfil( HttpServletRequest request )
    {
        String mlWssoProfilCode = request.getParameter( PARAMETER_MYLUTECE_WSSO_PROFIL_CODE );

        if ( mlWssoProfilCode == null )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_ERROR_REMOVE_PROFIL, AdminMessage.TYPE_STOP );
        }

        WssoProfil wssoProfil = WssoProfilHome.findWssoProfilByCode( mlWssoProfilCode, getPlugin( ) );

        if ( wssoProfil == null )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_ERROR_REMOVE_PROFIL, AdminMessage.TYPE_STOP );
        }

        WssoProfilHome.remove( wssoProfil, getPlugin( ) );

        return MANAGE_PROFILS + "?" + PARAMETER_PLUGIN_NAME + "=" + getPlugin( ).getName( );
    }

    /**
     * Returns users management form
     * 
     * @param request The Http request
     * @return Html form
     */
    public String getManageUsers( HttpServletRequest request )
    {
        setPageTitleProperty( PROPERTY_PAGE_TITLE_MANAGE_USERS );

        AdminUser user = AdminUserService.getAdminUser( request );

        boolean rightRoles = false;

        if ( user.checkRight( RIGHT_MANAGE_WSSO_ROLES ) )
        {
            rightRoles = true;
        }

        String strEmail = StringUtils.defaultString( request.getParameter( MARK_EMAIL ) );
        String strUserLastName = StringUtils.defaultString( request.getParameter( MARK_LAST_NAME ) );
        String strUserFirstName = StringUtils.defaultString( request.getParameter( MARK_FIRST_NAME ) );

        Collection<WssoUser> userList = WssoUserHome.findWssoUsersByLastNameOrFirstNameOrEmailByProfil(
                strUserLastName, strUserFirstName, strEmail, getPlugin( ) );
        Map<String, Object> model = new HashMap<String, Object>( );
        model.put( MARK_USERS_LIST, userList );
        model.put( MARK_PLUGIN_NAME, getPlugin( ).getName( ) );
        model.put( MARK_RIGHT_MANAGE_ROLES, rightRoles );
        model.put( MARK_EMAIL, strEmail );
        model.put( MARK_LAST_NAME, strUserLastName );
        model.put( MARK_FIRST_NAME, strUserFirstName );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MANAGE_USERS, getLocale( ), model );

        return getAdminPage( template.getHtml( ) );
    }

    /**
     * Returns profils management form
     * 
     * @param request The Http request
     * @return Html form
     */
    public String getManageProfils( HttpServletRequest request )
    {
        setPageTitleProperty( PROPERTY_PAGE_TITLE_MANAGE_PROFILS );

        HashMap<String, Object> model = new HashMap<String, Object>( );

        // Reinit session
        reinitItemNavigators( );

        String strURL = getHomeUrl( request );
        UrlItem url = new UrlItem( strURL );

        _nDefaultItemsPerPage = AppPropertiesService.getPropertyInt( PROPERTY_PROFILS_PER_PAGE, 50 );
        _strCurrentPageIndex = Paginator.getPageIndex( request, Paginator.PARAMETER_PAGE_INDEX, _strCurrentPageIndex );
        _nItemsPerPage = Paginator.getItemsPerPage( request, Paginator.PARAMETER_ITEMS_PER_PAGE, _nItemsPerPage,
                _nDefaultItemsPerPage );

        // Get profils
        List<WssoProfil> profilList = new ArrayList<WssoProfil>( );

        String searchCode = request.getParameter( MARK_AND_PARAMETER_SEARCH_CODE );
        String searchDescription = request.getParameter( MARK_AND_PARAMETER_SEARCH_DESCRIPTION );

        if ( StringUtils.isNotBlank( searchCode ) && StringUtils.isNotBlank( searchDescription ) )
        {
            WssoProfil wssoProfil = WssoProfilHome.findWssoProfilByCodeAndDescription( searchCode.trim( ),
                    searchDescription.trim( ), getPlugin( ) );

            if ( wssoProfil != null )
            {
                profilList.add( wssoProfil );
            }
        }
        else if ( StringUtils.isNotBlank( searchCode ) && StringUtils.isBlank( searchDescription ) )
        {
            WssoProfil wssoProfil = WssoProfilHome.findWssoProfilByCode( searchCode.trim( ), getPlugin( ) );

            if ( wssoProfil != null )
            {
                profilList.add( wssoProfil );
            }
        }
        else if ( StringUtils.isBlank( searchCode ) && StringUtils.isNotBlank( searchDescription ) )
        {
            List<WssoProfil> profilsByDescription = WssoProfilHome.findWssoProfilsByDescription(
                    searchDescription.trim( ), getPlugin( ) );
            profilList.addAll( profilsByDescription );
        }
        else
        {
            List<WssoProfil> allProfils = WssoProfilHome.findWssoProfilsList( getPlugin( ) );
            profilList.addAll( allProfils );
        }

        // SORT
        _strSortedAttributeName = request.getParameter( Parameters.SORTED_ATTRIBUTE_NAME );

        String strAscSort = null;

        if ( _strSortedAttributeName != null )
        {
            strAscSort = request.getParameter( Parameters.SORTED_ASC );

            _bIsAscSort = Boolean.parseBoolean( strAscSort );

            Collections.sort( profilList, new AttributeComparator( _strSortedAttributeName, _bIsAscSort ) );
        }

        if ( _strSortedAttributeName != null )
        {
            url.addParameter( Parameters.SORTED_ATTRIBUTE_NAME, _strSortedAttributeName );
        }

        if ( strAscSort != null )
        {
            url.addParameter( Parameters.SORTED_ASC, strAscSort );
        }

        LocalizedPaginator<WssoProfil> paginator = new LocalizedPaginator<WssoProfil>( profilList, _nItemsPerPage,
                url.getUrl( ), Paginator.PARAMETER_PAGE_INDEX, _strCurrentPageIndex, getLocale( ) );

        model.put( MARK_PROFILS_LIST, profilList );
        model.put( MARK_PLUGIN_NAME, getPlugin( ).getName( ) );
        model.put( MARK_AND_PARAMETER_SEARCH_CODE, searchCode );
        model.put( MARK_AND_PARAMETER_SEARCH_DESCRIPTION, searchDescription );
        model.put( MARK_NB_ITEMS_PER_PAGE, Integer.toString( _nItemsPerPage ) );
        model.put( MARK_PAGINATOR, paginator );
        model.put( MARK_PROFILS_LIST, paginator.getPageItems( ) );
        model.put( MARK_PLUGIN_NAME, getPlugin( ).getName( ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MANAGE_PROFILS, getLocale( ), model );

        return getAdminPage( template.getHtml( ) );
    }

    /**
     * Returns the profil modification form
     * 
     * @param request The Http request
     * @return Html modification form
     */
    public String getModifyProfil( HttpServletRequest request )
    {
        setPageTitleProperty( PROPERTY_PAGE_TITLE_MANAGE_PROFILS );

        String mlWssoProfilCode = request.getParameter( PARAMETER_MYLUTECE_WSSO_PROFIL_CODE );

        if ( mlWssoProfilCode == null )
        {
            return getManageProfils( request );
        }

        WssoProfil wssoProfil = WssoProfilHome.findWssoProfilByCode( mlWssoProfilCode, getPlugin( ) );

        if ( wssoProfil == null )
        {
            return getManageProfils( request );
        }

        // ITEM NAVIGATION
        List<WssoProfil> allProfils = WssoProfilHome.findWssoProfilsList( getPlugin( ) );
        setItemNavigator( mlWssoProfilCode, AppPathService.getBaseUrl( request )
                + "jsp/admin/plugins/mylutece/modules/wssodatabase/ModifyProfil.jsp", allProfils );

        Map<String, Object> model = new HashMap<String, Object>( );

        model.put( MARK_PLUGIN_NAME, getPlugin( ).getName( ) );
        model.put( MARK_PROFIL, wssoProfil );
        model.put( MARK_LOCALE, getLocale( ) );
        model.put( MARK_ITEM_NAVIGATOR, _itemNavigator );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MODIFY_PROFIL, getLocale( ), model );

        return getAdminPage( template.getHtml( ) );
    }

    /**
     * Reinit the item navigator
     */
    private void reinitItemNavigators( )
    {
        _strSortedAttributeName = StringUtils.EMPTY;
        _bIsAscSort = true;
    }

    /**
     * Returns roles management form for a specified user
     * 
     * @param request The Http request
     * @return Html form
     */
    public String getManageRolesUser( HttpServletRequest request )
    {
        AdminUser adminUser = getUser( );

        setPageTitleProperty( PROPERTY_PAGE_TITLE_MANAGE_ROLES_USER );

        int nUserId = Integer.parseInt( request.getParameter( PARAMETER_MYLUTECE_WSSO_USER_ID ) );

        WssoUser user = WssoUserHome.findByPrimaryKey( nUserId, getPlugin( ) );

        Collection<Role> allRoleList = RoleHome.findAll( );
        allRoleList = (ArrayList<Role>) RBACService.getAuthorizedCollection( allRoleList,
                RoleResourceIdService.PERMISSION_ASSIGN_ROLE, adminUser );

        Collection<String> userRoleList = WssoUserRoleHome.findRolesListForUser( nUserId, getPlugin( ) );

        List<WssoUser> allUsers = new ArrayList<WssoUser>( );
        allUsers.addAll( WssoUserHome.findWssoUsersList( getPlugin( ) ) );
        setItemNavigator( String.valueOf( nUserId ), AppPathService.getBaseUrl( request )
                + "jsp/admin/plugins/mylutece/modules/wssodatabase/ManageRolesUser.jsp", allUsers );

        Map<String, Object> model = new HashMap<String, Object>( );
        model.put( MARK_ROLES_LIST, allRoleList );
        model.put( MARK_ROLES_LIST_FOR_USER, userRoleList );
        model.put( MARK_USER, user );
        model.put( MARK_PLUGIN_NAME, getPlugin( ).getName( ) );
        model.put( MARK_ITEM_NAVIGATOR, _itemNavigator );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MANAGE_ROLES_USER, getLocale( ), model );

        return getAdminPage( template.getHtml( ) );
    }

    /**
     * Process assignation roles for a specified user
     * 
     * @param request The Http request
     * @return Html form
     */
    public String doAssignRoleUser( HttpServletRequest request )
    {
        int nUserId = Integer.parseInt( request.getParameter( PARAMETER_MYLUTECE_WSSO_USER_ID ) );
        WssoUser user = WssoUserHome.findByPrimaryKey( nUserId, getPlugin( ) );

        String[] roleArray = request.getParameterValues( PARAMETER_MYLUTECE_WSSO_ROLE_ID );

        WssoUserRoleHome.deleteRolesForUser( user.getMyluteceWssoUserId( ), getPlugin( ) );

        if ( roleArray != null )
        {
            for ( int i = 0; i < roleArray.length; i++ )
            {
                WssoUserRoleHome.createRoleForUser( nUserId, roleArray[i], getPlugin( ) );
            }
        }

        return MANAGE_USERS + "?" + PARAMETER_PLUGIN_NAME + "=" + getPlugin( ).getName( );
    }

    /**
     * Get the item navigator
     * @param idElement the id of the element
     * @param strUrl the url
     * @param listElements the list of elements
     */
    private void setItemNavigator( String idElement, String strUrl, List<?> listElements )
    {
        if ( _itemNavigator == null )
        {
            List<String> listIds = new ArrayList<String>( );
            int nCurrentItemId = 0;
            int nIndex = 0;
            boolean profil = false;
            boolean user = false;

            for ( Object element : listElements )
            {
                if ( element instanceof WssoProfil )
                {
                    String code = ( (WssoProfil) element ).getCode( );
                    listIds.add( code );

                    if ( code.equals( idElement ) )
                    {
                        nCurrentItemId = nIndex;
                    }

                    profil = true;
                    nIndex++;
                }
                else if ( element instanceof WssoUser )
                {
                    String idUser = String.valueOf( ( (WssoUser) element ).getMyluteceWssoUserId( ) );
                    listIds.add( idUser );

                    if ( idUser.equals( idElement ) )
                    {
                        nCurrentItemId = nIndex;
                    }

                    user = true;
                    nIndex++;
                }
            }

            String iterateur = "";

            if ( profil )
            {
                iterateur = PARAMETER_MYLUTECE_WSSO_PROFIL_CODE;
            }
            else if ( user )
            {
                iterateur = PARAMETER_MYLUTECE_WSSO_USER_ID;
            }

            _itemNavigator = new ItemNavigator( listIds, nCurrentItemId, strUrl, iterateur );
        }
        else
        {
            _itemNavigator.setCurrentItemId( idElement );
            _itemNavigator.setBaseUrl( strUrl );
        }
    }

    /**
     * Returns roles management form for a specified profil
     * 
     * @param request The Http request
     * @return Html form
     */
    public String getManageRolesProfil( HttpServletRequest request )
    {
        setPageTitleProperty( PROPERTY_PAGE_TITLE_MANAGE_ROLES_PROFIL );

        String mlWssoProfilCode = request.getParameter( PARAMETER_MYLUTECE_WSSO_PROFIL_CODE );

        if ( mlWssoProfilCode == null )
        {
            return getManageProfils( request );
        }

        WssoProfil wssoProfil = WssoProfilHome.findWssoProfilByCode( mlWssoProfilCode, getPlugin( ) );

        if ( wssoProfil == null )
        {
            return getManageProfils( request );
        }

        Collection<Role> allRoleList = RoleHome.findAll( );

        List<String> profilRoleKeyList = IdxWSSODatabaseHome.findRolesFromProfil( wssoProfil.getCode( ), getPlugin( ) );
        Collection<Role> profilRoleList = new ArrayList<Role>( );

        for ( String strRoleKey : profilRoleKeyList )
        {
            for ( Role role : allRoleList )
            {
                if ( role.getRole( ).equals( strRoleKey ) )
                {
                    profilRoleList.add( RoleHome.findByPrimaryKey( strRoleKey ) );
                }
            }
        }

        // ITEM NAVIGATION
        List<WssoProfil> allProfils = WssoProfilHome.findWssoProfilsList( getPlugin( ) );
        setItemNavigator( mlWssoProfilCode, AppPathService.getBaseUrl( request )
                + "jsp/admin/plugins/mylutece/modules/wssodatabase/ManageRolesProfil.jsp", allProfils );

        Map<String, Object> model = new HashMap<String, Object>( );
        model.put( MARK_ROLES_LIST, allRoleList );
        model.put( MARK_ROLES_LIST_FOR_USER, profilRoleList );
        model.put( MARK_PROFIL, wssoProfil );
        model.put( MARK_PLUGIN_NAME, getPlugin( ).getName( ) );
        model.put( MARK_ITEM_NAVIGATOR, _itemNavigator );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MANAGE_ROLES_PROFIL, getLocale( ), model );

        return getAdminPage( template.getHtml( ) );
    }

    /**
     * Assign roles to a profil
     * @param request HttpServletRequest
     * @return JSP return
     */
    public String doManageRolesProfil( HttpServletRequest request )
    {
        String mlWssoProfilCode = request.getParameter( PARAMETER_MYLUTECE_WSSO_PROFIL_CODE );

        if ( mlWssoProfilCode == null )
        {
            return getManageProfils( request );
        }

        WssoProfil wssoProfil = WssoProfilHome.findWssoProfilByCode( mlWssoProfilCode, getPlugin( ) );

        if ( wssoProfil == null )
        {
            return getManageProfils( request );
        }

        String[] roleArray = request.getParameterValues( PARAMETER_MYLUTECE_DATABASE_ROLE_IDS );

        IdxWSSODatabaseHome.removeRolesForProfil( wssoProfil.getCode( ), getPlugin( ) );

        if ( roleArray != null )
        {
            for ( int i = 0; i < roleArray.length; i++ )
            {
                IdxWSSODatabaseHome.addRoleForProfil( wssoProfil.getCode( ), roleArray[i], getPlugin( ) );
            }
        }

        return MANAGE_ROLES_PROFIL + "?" + PARAMETER_PLUGIN_NAME + "=" + getPlugin( ).getName( ) + "&"
                + PARAMETER_MYLUTECE_WSSO_PROFIL_CODE + "=" + mlWssoProfilCode;
    }

    /**
     * Returns roles management form for a specified user
     * 
     * @param request The Http request
     * @return Html form
     */
    public String getManageProfilsUser( HttpServletRequest request )
    {
        setPageTitleProperty( PROPERTY_PAGE_TITLE_MANAGE_PROFILS_USER );

        String strUserId = request.getParameter( PARAMETER_MYLUTECE_WSSO_USER_ID );

        if ( StringUtils.isBlank( strUserId ) )
        {
            return getManageUsers( request );
        }

        int nUserId = Integer.parseInt( strUserId );

        WssoUser wssoUser = WssoUserHome.findByPrimaryKey( nUserId, getPlugin( ) );

        if ( wssoUser == null )
        {
            return getManageUsers( request );
        }

        Collection<WssoProfil> allWssoProfilList = WssoProfilHome.findWssoProfilsList( getPlugin( ) );

        List<String> profilKeyList = WssoProfilHome.findWssoProfilsForUser( wssoUser.getMyluteceWssoUserId( ),
                getPlugin( ) );

        Collection<WssoProfil> profilList = new ArrayList<WssoProfil>( );

        for ( String strProfilKey : profilKeyList )
        {
            for ( WssoProfil wssoProfil : allWssoProfilList )
            {
                if ( wssoProfil.getCode( ).equals( strProfilKey ) )
                {
                    profilList.add( WssoProfilHome.findWssoProfilByCode( strProfilKey, getPlugin( ) ) );
                }
            }
        }

        // ITEM NAVIGATION
        List<WssoUser> allUsers = new ArrayList<WssoUser>( );
        allUsers.addAll( WssoUserHome.findWssoUsersList( getPlugin( ) ) );
        setItemNavigator( strUserId, AppPathService.getBaseUrl( request )
                + "jsp/admin/plugins/mylutece/modules/wssodatabase/ManageProfilsUser.jsp", allUsers );

        AdminUser user = AdminUserService.getAdminUser( request );

        boolean rightRoles = false;

        if ( user.checkRight( RIGHT_MANAGE_WSSO_ROLES ) )
        {
            rightRoles = true;
        }

        Map<String, Object> model = new HashMap<String, Object>( );
        model.put( MARK_PROFILS_LIST, allWssoProfilList );
        model.put( MARK_PROFILS_LIST_FOR_USER, profilList );
        model.put( MARK_USER, wssoUser );
        model.put( MARK_PLUGIN_NAME, getPlugin( ).getName( ) );
        model.put( MARK_ITEM_NAVIGATOR, _itemNavigator );
        model.put( MARK_RIGHT_MANAGE_ROLES, rightRoles );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MANAGE_PROFILS_USER, getLocale( ), model );

        return getAdminPage( template.getHtml( ) );
    }

    /**
     * Assign profils to a user
     * @param request HttpServletRequest
     * @return JSP return
     */
    public String doManageProfilsUser( HttpServletRequest request )
    {
        String strUserId = request.getParameter( PARAMETER_MYLUTECE_WSSO_USER_ID );

        if ( StringUtils.isBlank( strUserId ) )
        {
            return getManageUsers( request );
        }

        int nWssoUserId = Integer.parseInt( strUserId );

        WssoUser wssoUser = WssoUserHome.findByPrimaryKey( nWssoUserId, getPlugin( ) );

        if ( wssoUser == null )
        {
            return getManageUsers( request );
        }

        String[] profilArray = request.getParameterValues( PARAMETER_MYLUTECE_WSSO_PROFIL_CODE );

        IdxWSSODatabaseHome.removeProfilsForUser( nWssoUserId, getPlugin( ) );

        if ( profilArray != null )
        {
            for ( int i = 0; i < profilArray.length; i++ )
            {
                IdxWSSODatabaseHome.addUserForProfil( nWssoUserId, profilArray[i], getPlugin( ) );
            }
        }

        return MANAGE_PROFILS_USER + "?" + PARAMETER_MYLUTECE_WSSO_USER_ID + "=" + nWssoUserId;
    }

    /**
     * Returns users management form for a specified profil
     * 
     * @param request The Http request
     * @return Html form
     */
    public String getManageUsersProfil( HttpServletRequest request )
    {
        setPageTitleProperty( PROPERTY_PAGE_TITLE_MANAGE_USERS_PROFIL );

        String mlWssoProfilCode = request.getParameter( PARAMETER_MYLUTECE_WSSO_PROFIL_CODE );

        if ( mlWssoProfilCode == null )
        {
            return getManageProfils( request );
        }

        WssoProfil wssoProfil = WssoProfilHome.findWssoProfilByCode( mlWssoProfilCode, getPlugin( ) );

        if ( wssoProfil == null )
        {
            return getManageProfils( request );
        }

        Map<String, Object> model = new HashMap<String, Object>( );
        String strURL = AppPathService.getBaseUrl( request ) + JSP_URL_MANAGE_USERS_PROFIL;
        UrlItem url = new UrlItem( strURL );

        String searchLastName = request.getParameter( "search_last_name" );
        String searchFirstName = request.getParameter( "search_first_name" );
        String searchEmail = request.getParameter( "search_email" );

        // ASSIGNED USERS
        List<WssoUser> listAllAssignedUsers = WssoUserHome.findWssoUserssByLastNameOrFirtNameOrEmailByProfil(
                wssoProfil.getCode( ), searchLastName, searchFirstName, searchEmail, getPlugin( ) );

        // AVAILABLE USERS
        ReferenceList listAvailableUsers = getAvailableUsers( listAllAssignedUsers );

        // SORT
        String strSortedAttributeName = request.getParameter( Parameters.SORTED_ATTRIBUTE_NAME );
        String strAscSort = null;

        if ( strSortedAttributeName != null )
        {
            strAscSort = request.getParameter( Parameters.SORTED_ASC );

            boolean bIsAscSort = Boolean.parseBoolean( strAscSort );

            Collections.sort( listAvailableUsers, new AttributeComparator( strSortedAttributeName, bIsAscSort ) );
        }

        _strCurrentPageIndex = Paginator.getPageIndex( request, Paginator.PARAMETER_PAGE_INDEX, _strCurrentPageIndex );
        _nDefaultItemsPerPage = AppPropertiesService.getPropertyInt( PROPERTY_PROFILS_PER_PAGE, 50 );
        _nItemsPerPage = Paginator.getItemsPerPage( request, Paginator.PARAMETER_ITEMS_PER_PAGE, _nItemsPerPage,
                _nDefaultItemsPerPage );

        if ( strSortedAttributeName != null )
        {
            url.addParameter( Parameters.SORTED_ATTRIBUTE_NAME, strSortedAttributeName );
        }

        if ( strAscSort != null )
        {
            url.addParameter( Parameters.SORTED_ASC, strAscSort );
        }

        // ITEM NAVIGATION
        setItemNavigator( mlWssoProfilCode, AppPathService.getBaseUrl( request )
                + "jsp/admin/plugins/mylutece/modules/wssodatabase/ManageUsersProfil.jsp", listAllAssignedUsers );

        LocalizedPaginator<WssoUser> paginator = new LocalizedPaginator<WssoUser>( listAllAssignedUsers,
                _nItemsPerPage, url.getUrl( ), Paginator.PARAMETER_PAGE_INDEX, _strCurrentPageIndex, getLocale( ) );

        model.put( MARK_PLUGIN_NAME, getPlugin( ).getName( ) );
        model.put( MARK_PROFIL, wssoProfil );
        model.put( MARK_ITEM_NAVIGATOR, _itemNavigator );
        model.put( MARK_PAGINATOR, paginator );
        model.put( MARK_NB_ITEMS_PER_PAGE, Integer.toString( _nItemsPerPage ) );
        model.put( MARK_AVAILABLE_USERS, listAvailableUsers );
        model.put( MARK_ASSIGNED_USERS, paginator.getPageItems( ) );
        model.put( MARK_ASSIGNED_USERS_NUMBER, listAllAssignedUsers.size( ) );
        model.put( MARK_AND_PARAMETER_SEARCH_LASTNAME, searchLastName );
        model.put( MARK_AND_PARAMETER_SEARCH_FIRSTNAME, searchFirstName );
        model.put( MARK_AND_PARAMETER_SEARCH_EMAIL, searchEmail );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MANAGE_USERS_PROFIL, getLocale( ), model );

        return getAdminPage( template.getHtml( ) );
    }

    /**
     * Get the list of avaivable users
     * @param listAssignedUsers the list of assigned users
     * @return a {@link ReferenceList}
     */
    private ReferenceList getAvailableUsers( List<WssoUser> listAssignedUsers )
    {
        ReferenceList listAvailableUsers = new ReferenceList( );

        for ( WssoUser user : (Collection<WssoUser>) WssoUserHome.findWssoUsersList( getPlugin( ) ) )
        {
            boolean bIsAvailable = Boolean.TRUE;

            for ( WssoUser assignedUser : listAssignedUsers )
            {
                if ( user.getMyluteceWssoUserId( ) == assignedUser.getMyluteceWssoUserId( ) )
                {
                    bIsAvailable = Boolean.FALSE;

                    break;
                }
            }

            if ( bIsAvailable )
            {
                ReferenceItem userItem = new ReferenceItem( );
                userItem.setCode( String.valueOf( user.getMyluteceWssoUserId( ) ) );
                userItem.setName( user.getLastName( ) + SPACE + user.getFirstName( ) );
                listAvailableUsers.add( userItem );
            }
        }

        return listAvailableUsers;
    }

    /**
     * Assign users to a profil
     * @param request HttpServletRequest
     * @return JSP return
     */
    public String doManageUsersProfil( HttpServletRequest request )
    {
        setPageTitleProperty( PROPERTY_PAGE_TITLE_MANAGE_PROFILS );

        String mlWssoProfilCode = request.getParameter( PARAMETER_MYLUTECE_WSSO_PROFIL_CODE );

        if ( mlWssoProfilCode == null )
        {
            return getManageProfils( request );
        }

        WssoProfil wssoProfil = WssoProfilHome.findWssoProfilByCode( mlWssoProfilCode, getPlugin( ) );

        if ( wssoProfil == null )
        {
            return getManageProfils( request );
        }

        //retrieve the selected portlets ids
        String[] arrayUsersIds = request.getParameterValues( PARAMETER_AVAILABLE_USERS );

        if ( ( arrayUsersIds != null ) )
        {
            for ( int i = 0; i < arrayUsersIds.length; i++ )
            {
                int nUserId = Integer.parseInt( arrayUsersIds[i] );
                WssoUser user = WssoUserHome.findByPrimaryKey( nUserId, getPlugin( ) );

                if ( user != null )
                {
                    IdxWSSODatabaseHome.addUserForProfil( nUserId, wssoProfil.getCode( ), getPlugin( ) );
                }

            }
        }

        return getManageUsersProfil( request );
    }

    /**
     * Returns removal user's form
     * 
     * @param request The Http request
     * @return Html form
     */
    public String getUnassignUserProfil( HttpServletRequest request )
    {
        String mlWssoProfilCode = request.getParameter( PARAMETER_MYLUTECE_WSSO_PROFIL_CODE );

        if ( mlWssoProfilCode == null )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_ERROR_REMOVE_PROFIL, AdminMessage.TYPE_STOP );
        }

        String assignedUseId = request.getParameter( MARK_AND_PARAMETER_ASSIGNED_USER_ID );

        if ( assignedUseId == null )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_ERROR_REMOVE_PROFIL, AdminMessage.TYPE_STOP );
        }

        UrlItem url = new UrlItem( JSP_DO_REMOVE_ASIGN_USER_PROFIL );
        url.addParameter( PARAMETER_PLUGIN_NAME, getPlugin( ).getName( ) );
        url.addParameter( PARAMETER_MYLUTECE_WSSO_PROFIL_CODE, mlWssoProfilCode );
        url.addParameter( MARK_AND_PARAMETER_ASSIGNED_USER_ID, assignedUseId );

        return AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_ASSIGN_PROFIL_USER, url.getUrl( ),
                AdminMessage.TYPE_CONFIRMATION );
    }

    /**
     * Process user's removal
     * 
     * @param request The Http request
     * @return The Jsp management URL of the process result
     */
    public String doUnassignUserProfil( HttpServletRequest request )
    {
        String mlWssoProfilCode = request.getParameter( PARAMETER_MYLUTECE_WSSO_PROFIL_CODE );

        if ( mlWssoProfilCode == null )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_ERROR_REMOVE_PROFIL, AdminMessage.TYPE_STOP );
        }

        WssoProfil wssoProfil = WssoProfilHome.findWssoProfilByCode( mlWssoProfilCode, getPlugin( ) );

        if ( wssoProfil == null )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_ERROR_REMOVE_PROFIL, AdminMessage.TYPE_STOP );
        }

        String assignedUseId = request.getParameter( MARK_AND_PARAMETER_ASSIGNED_USER_ID );

        if ( assignedUseId == null )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_ERROR_REMOVE_PROFIL, AdminMessage.TYPE_STOP );
        }

        WssoUser assignedUser = WssoUserHome.findByPrimaryKey( Integer.valueOf( assignedUseId ), getPlugin( ) );

        if ( assignedUser == null )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_ERROR_REMOVE_PROFIL, AdminMessage.TYPE_STOP );
        }

        IdxWSSODatabaseHome.removeUserForProfil( assignedUser.getMyluteceWssoUserId( ), wssoProfil.getCode( ),
                getPlugin( ) );

        return getManageUsersProfil( request );
    }

    /**
     * Get a page to export users
     * @param request The request
     * @return The html content
     */
    public String getExportUsers( HttpServletRequest request )
    {
        setPageTitleProperty( PROPERTY_PAGE_TITLE_MANAGE_USERS );

        Map<String, Object> model = new HashMap<String, Object>( );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_EXPORT_USERS_FROM_FILE, AdminUserService.getLocale( request ), model );

        return getAdminPage( template.getHtml( ) );
    }

    /**
     * Do export users
     * @param request The request
     * @param response The response
     * @return A DefaultPluginActionResult containing the result, or null if the
     *         file download has been initialized
     * @throws IOException If an IOException occurs
     */
    public DefaultPluginActionResult doExportUsers( HttpServletRequest request, HttpServletResponse response )
            throws IOException
    {
        Plugin plugin = getPlugin( );

        String strExportProfils = request.getParameter( PARAMETER_EXPORT_PROFILS );
        String strExportRoles = request.getParameter( PARAMETER_EXPORT_ROLES );
        String strExportGroups = request.getParameter( PARAMETER_EXPORT_GROUPS );
        boolean bExportProfils = StringUtils.isNotEmpty( strExportProfils );
        boolean bExportRoles = StringUtils.isNotEmpty( strExportRoles );

        Collection<WssoUser> listUsers = WssoUserHome.findWssoUsersList( plugin );

        List<WssoUserWrapper> listUserExport = new ArrayList<>( );

        for ( WssoUser user : listUsers )
        {
            listUserExport.add( _databaseService.getExportUser( user, bExportRoles, bExportProfils ) );
        }

        Map<String, Object> model = new HashMap<>( );
        model.put( MARK_EXPORT_USERS, listUserExport );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_EXPORT_USERS, getLocale( ), model );
        String strExportedUsers = template.getHtml( );

        response.setContentType( CONSTANT_MIME_TYPE_CSV );
        String strFileName = CONSTANT_EXPORT_USERS_FILE_NAME + CONSTANT_EXTENSION_CSV_FILE;
        response.setHeader( CONSTANT_ATTACHEMENT_DISPOSITION, CONSTANT_ATTACHEMENT_FILE_NAME + strFileName + CONSTANT_QUOTE );

        PrintWriter out = response.getWriter( );
        out.write( StringUtils.trim( strExportedUsers ) );
        out.flush( );
        out.close( );

        return null;
    }

    /**
     * Get a page to import users from a CSV file.
     * @param request The request
     * @return The HTML content
     */
    public String getImportUsersFromFile( HttpServletRequest request )
    {
        setPageTitleProperty( PROPERTY_IMPORT_USERS_FROM_FILE_PAGETITLE );

        Map<String, Object> model = new HashMap<String, Object>( );

        model.put( MARK_LIST_MESSAGES, request.getAttribute( ATTRIBUTE_IMPORT_USERS_LIST_MESSAGES ) );

        String strCsvSeparator = StringUtils.EMPTY + _importWssoDatabaseUserService.getCSVSeparator( );
        String strCsvEscapeCharacter = StringUtils.EMPTY + _importWssoDatabaseUserService.getCSVEscapeCharacter( );
        String strAttributesSeparator = StringUtils.EMPTY + _importWssoDatabaseUserService.getAttributesSeparator( );
        model.put( MARK_CSV_SEPARATOR, strCsvSeparator );
        model.put( MARK_CSV_ESCAPE, strCsvEscapeCharacter );
        model.put( MARK_ATTRIBUTES_SEPARATOR, strAttributesSeparator );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_IMPORT_USERS_FROM_FILE,
                AdminUserService.getLocale( request ), model );

        return getAdminPage( template.getHtml( ) );
    }

    /**
     * Do import users from a CSV file
     * @param request The request
     * @return A DefaultPluginActionResult with the URL of the page to display,
     *         or the HTML content
     */
    public DefaultPluginActionResult doImportUsersFromFile( HttpServletRequest request )
    {
        DefaultPluginActionResult result = new DefaultPluginActionResult( );

        if ( request instanceof MultipartHttpServletRequest )
        {
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
            FileItem fileItem = multipartRequest.getFile( PARAMETER_IMPORT_USERS_FILE );
            String strMimeType = FileSystemUtil.getMIMEType( FileUploadService.getFileNameOnly( fileItem ) );

            if ( !( ( fileItem != null ) && !StringUtils.EMPTY.equals( fileItem.getName( ) ) ) )
            {
                Object[] tabRequiredFields = { I18nService.getLocalizedString( FIELD_IMPORT_USERS_FILE, getLocale( ) ) };
                result.setRedirect( AdminMessageService.getMessageUrl( request, MESSAGE_MANDATORY_FIELD,
                        tabRequiredFields, AdminMessage.TYPE_STOP ) );

                return result;
            }

            if ( ( !strMimeType.equals( CONSTANT_MIME_TYPE_CSV )
                    && !strMimeType.equals( CONSTANT_MIME_TYPE_OCTETSTREAM ) && !strMimeType
                        .equals( CONSTANT_MIME_TYPE_TEXT_CSV ) )
                    || !fileItem.getName( ).toLowerCase( ).endsWith( CONSTANT_EXTENSION_CSV_FILE ) )
            {
                result.setRedirect( AdminMessageService.getMessageUrl( request, MESSAGE_ERROR_CSV_FILE_IMPORT,
                        AdminMessage.TYPE_STOP ) );

                return result;
            }

            String strSkipFirstLine = multipartRequest.getParameter( PARAMETER_SKIP_FIRST_LINE );
            boolean bSkipFirstLine = StringUtils.isNotEmpty( strSkipFirstLine );
            String strUpdateUsers = multipartRequest.getParameter( PARAMETER_UPDATE_USERS );
            boolean bUpdateUsers = StringUtils.isNotEmpty( strUpdateUsers );
            _importWssoDatabaseUserService.setUpdateExistingUsers( bUpdateUsers );

            List<CSVMessageDescriptor> listMessages = _importWssoDatabaseUserService.readCSVFile( fileItem, 0, false,
                    false, bSkipFirstLine, AdminUserService.getLocale( request ), AppPathService.getBaseUrl( request ) );

            request.setAttribute( ATTRIBUTE_IMPORT_USERS_LIST_MESSAGES, listMessages );

            String strHtmlResult = getImportUsersFromFile( request );
            result.setHtmlContent( strHtmlResult );
        }
        else
        {
            Object[] tabRequiredFields = { I18nService.getLocalizedString( FIELD_IMPORT_USERS_FILE, getLocale( ) ) };
            result.setRedirect( AdminMessageService.getMessageUrl( request, MESSAGE_MANDATORY_FIELD, tabRequiredFields,
                    AdminMessage.TYPE_STOP ) );
        }

        return result;
    }

    /**
     * Notify a user of the creation of his account and give him his credentials
     * @param user the user to notify
     * @param locale The locale
     * @param strProdUrl The prod URL
     */
    private void notifyUserAccountCreated( WssoUser user, Locale locale, String strProdUrl )
    {
        String strSenderEmail = AppPropertiesService.getProperty( PROPERTY_NO_REPLY_EMAIL );
        String strSiteName = AppPropertiesService.getProperty( PROPERTY_SITE_NAME );

        String strEmailSubject = I18nService.getLocalizedString( MESSAGE_ACCOUNT_IMPORTED_MAIL_SUBJECT,
                new String[] { strSiteName }, locale );
        String strBaseURL = strProdUrl;
        Map<String, Object> model = new HashMap<String, Object>( );
        model.put( MARK_USER, user );
        model.put( MARK_SITE_NAME, strSiteName );
        model.put( MARK_SITE_LINK, MailService.getSiteLink( strBaseURL, true ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MAIL_USER_IMPORTED, locale, model );

        MailService
                .sendMailHtml( user.getEmail( ), strSenderEmail, strSenderEmail, strEmailSubject, template.getHtml( ) );
    }
}
