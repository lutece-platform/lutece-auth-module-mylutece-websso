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
package fr.paris.lutece.plugins.mylutece.modules.wssodatabase.authentication.service;

import fr.paris.lutece.plugins.mylutece.modules.wssodatabase.authentication.business.IdxWSSODatabaseHome;
import fr.paris.lutece.plugins.mylutece.modules.wssodatabase.authentication.business.WssoProfil;
import fr.paris.lutece.plugins.mylutece.modules.wssodatabase.authentication.business.WssoProfilHome;
import fr.paris.lutece.plugins.mylutece.modules.wssodatabase.authentication.business.WssoUser;
import fr.paris.lutece.plugins.mylutece.modules.wssodatabase.authentication.business.WssoUserHome;
import fr.paris.lutece.plugins.mylutece.modules.wssodatabase.authentication.business.WssoUserRoleHome;
import fr.paris.lutece.plugins.mylutece.service.attribute.MyLuteceUserFieldService;
import fr.paris.lutece.portal.business.role.Role;
import fr.paris.lutece.portal.business.role.RoleHome;
import fr.paris.lutece.portal.service.csv.CSVMessageDescriptor;
import fr.paris.lutece.portal.service.csv.CSVMessageLevel;
import fr.paris.lutece.portal.service.csv.CSVReaderService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.mail.MailService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.html.HtmlTemplate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;


/**
 * Import database users from a CSV file
 */
public class ImportWssoDatabaseUserService extends CSVReaderService
{
    private static final String MESSAGE_ACCESS_CODE_ALREADY_USED = "module.mylutece.wssodatabase.message.user_exist";
    private static final String MESSAGE_EMAIL_ALREADY_USED = "module.mylutece.wssodatabase.message.user_exist";
    private static final String MESSAGE_USERS_IMPORTED = "module.mylutece.wssodatabase.import_users_from_file.usersImported";
    private static final String MESSAGE_ERROR_MIN_NUMBER_COLUMNS = "module.mylutece.wssodatabase.import_users_from_file.messageErrorMinColumnNumber";
    private static final String MESSAGE_ACCOUNT_IMPORTED_MAIL_SUBJECT = "module.mylutece.wssodatabase.import_users_from_file.email.mailSubject";
    private static final String MESSAGE_ROLE_UNKNOWN = "module.mylutece.wssodatabase.message.import_user_role_unknown";

    //    private static final String MESSAGE_GROUP_UNKNOWN = "module.mylutece.wssodatabase.message.import_user_group_unknown";
    private static final String MESSAGE_PROFIL_UNKNOWN = "module.mylutece.wssodatabase.message.import_user_profil_unknown";
    private static final String MESSAGE_GUID_EMPTY = "module.mylutece.wssodatabase.message.import_user_guid_empty";
    private static final String MESSAGE_FIRST_NAME_EMPTY = "module.mylutece.wssodatabase.message.import_user_first_name_empty";
    private static final String MESSAGE_LAST_NAME_EMPTY = "module.mylutece.wssodatabase.message.import_user_last_name_empty";
    private static final String MESSAGE_EMAIL_EMPTY = "module.mylutece.wssodatabase.message.import_user_email_empty";
    private static final String MESSAGE_EMAIL_INVALID = "module.mylutece.wssodatabase.message.import_user_email_invalid";
    private static final String PROPERTY_NO_REPLY_EMAIL = "mail.noreply.email";
    private static final String PROPERTY_IMPORT_EXPORT_USER_SEPARATOR = "lutece.importExportUser.defaultSeparator";
    private static final String PROPERTY_SITE_NAME = "lutece.name";
    private static final String TEMPLATE_MAIL_USER_IMPORTED = "admin/plugins/mylutece/modules/wssodatabase/mail_user_imported.html";
    private static final String MARK_SITE_NAME = "site_name";
    private static final String MARK_USER = "user";
    private static final String MARK_SITE_LINK = "site_link";
    private static final String CONSTANT_DEFAULT_IMPORT_EXPORT_USER_SEPARATOR = ":";
    private static final String CONSTANT_ROLE = "role";
    private static final int CONSTANT_MINIMUM_COLUMNS_PER_LINE = 4;

    /**
     * Format d'un e-mail (texte@domain.extension)
     */
    private static final String CONSTANT_EMAIL = "(^([a-zA-Z0-9]+(([\\.\\-\\_]?[a-zA-Z0-9]+)+)?)\\@(([a-zA-Z0-9]+[\\.\\-\\_])+[a-zA-Z]{2,4})$)|(^$)";
    private Character _strAttributesSeparator;
    private boolean _bUpdateExistingUsers;

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<CSVMessageDescriptor> readLineOfCSVFile( String[] strLineDataArray, int nLineNumber, Locale locale,
            String strBaseUrl )
    {
        Plugin databasePlugin = PluginService.getPlugin( WssoDatabasePlugin.PLUGIN_NAME );
        List<CSVMessageDescriptor> listMessages = new ArrayList<CSVMessageDescriptor>( );
        int nIndex = 0;

        String strGuid = strLineDataArray[nIndex++];

        if ( StringUtils.isBlank( strGuid ) )
        {
            String strErrorMessage = I18nService.getLocalizedString( MESSAGE_GUID_EMPTY, null, locale );
            CSVMessageDescriptor error = new CSVMessageDescriptor( CSVMessageLevel.ERROR, nLineNumber, strErrorMessage );
            listMessages.add( error );
        }

        String strLastName = strLineDataArray[nIndex++];

        if ( StringUtils.isBlank( strLastName ) )
        {
            String strErrorMessage = I18nService.getLocalizedString( MESSAGE_LAST_NAME_EMPTY, null, locale );
            CSVMessageDescriptor error = new CSVMessageDescriptor( CSVMessageLevel.ERROR, nLineNumber, strErrorMessage );
            listMessages.add( error );
        }

        String strFirstName = strLineDataArray[nIndex++];

        if ( StringUtils.isBlank( strFirstName ) )
        {
            String strErrorMessage = I18nService.getLocalizedString( MESSAGE_FIRST_NAME_EMPTY, null, locale );
            CSVMessageDescriptor error = new CSVMessageDescriptor( CSVMessageLevel.ERROR, nLineNumber, strErrorMessage );
            listMessages.add( error );
        }

        String strEmail = strLineDataArray[nIndex++];

        if ( StringUtils.isBlank( strEmail ) )
        {
            String strErrorMessage = I18nService.getLocalizedString( MESSAGE_EMAIL_EMPTY, null, locale );
            CSVMessageDescriptor error = new CSVMessageDescriptor( CSVMessageLevel.ERROR, nLineNumber, strErrorMessage );
            listMessages.add( error );
        }
        else
        {
            if ( !Pattern.matches( CONSTANT_EMAIL, strEmail ) )
            {
                String strErrorMessage = I18nService.getLocalizedString( MESSAGE_EMAIL_INVALID, null, locale );
                CSVMessageDescriptor error = new CSVMessageDescriptor( CSVMessageLevel.ERROR, nLineNumber,
                        strErrorMessage );
                listMessages.add( error );
            }
        }

        if ( CollectionUtils.isNotEmpty( listMessages ) )
        {
            return listMessages;
        }

        boolean bUpdateUser = getUpdateExistingUsers( );
        int nUserId = 0;

        if ( bUpdateUser )
        {
            int nAccessCodeUserId = WssoUserHome.findDatabaseUserIdFromGuid( strGuid, databasePlugin );

            if ( nAccessCodeUserId > 0 )
            {
                nUserId = nAccessCodeUserId;
            }

            bUpdateUser = nUserId > 0;
        }

        WssoUser wssoUser = new WssoUser( );

        wssoUser.setGuid( strGuid );
        wssoUser.setLastName( strLastName );
        wssoUser.setFirstName( strFirstName );
        wssoUser.setEmail( strEmail );

        if ( bUpdateUser )
        {
            wssoUser.setMyluteceWssoUserId( nUserId );
            // We update the user
            WssoUserHome.update( wssoUser, databasePlugin );
        }
        else
        {
            // We create the user
            WssoUserHome.create( wssoUser, databasePlugin );
            notifyUserAccountCreated( wssoUser, locale, AppPathService.getProdUrl( strBaseUrl ) );
        }

        // We remove old roles, groups and attributes of the user
        WssoUserRoleHome.deleteRolesForUser( wssoUser.getMyluteceWssoUserId( ), databasePlugin );
        //        DatabaseHome.removeGroupsForUser( user.getUserId( ), databasePlugin );
        IdxWSSODatabaseHome.removeProfilsForUser( wssoUser.getMyluteceWssoUserId( ), databasePlugin );
        MyLuteceUserFieldService.doRemoveUserFields( wssoUser.getMyluteceWssoUserId( ), locale );

        // We get every attributes, roles and groups of the user
        List<String> listRoles = new ArrayList<String>( );

        //        List<String> listGroups = new ArrayList<String>( );
        List<String> listProfils = new ArrayList<String>( );

        while ( nIndex < strLineDataArray.length )
        {
            String strValue = strLineDataArray[nIndex];

            if ( StringUtils.isNotBlank( strValue ) && ( strValue.indexOf( getAttributesSeparator( ) ) > 0 ) )
            {
                int nSeparatorIndex = strValue.indexOf( getAttributesSeparator( ) );
                String strLineId = strValue.substring( 0, nSeparatorIndex );

                if ( StringUtils.isNotBlank( strLineId ) )
                {
                    if ( StringUtils.equalsIgnoreCase( strLineId, CONSTANT_ROLE ) )
                    {
                        String strRole = strValue.substring( nSeparatorIndex + 1 );
                        Role role = RoleHome.findByPrimaryKey( strRole );

                        if ( role == null )
                        {
                            Object[] args = { strRole };
                            String strErrorMessage = I18nService
                                    .getLocalizedString( MESSAGE_ROLE_UNKNOWN, args, locale );
                            CSVMessageDescriptor error = new CSVMessageDescriptor( CSVMessageLevel.ERROR, nLineNumber,
                                    strErrorMessage );
                            listMessages.add( error );
                        }
                        else
                        {
                            listRoles.add( strRole );
                        }
                    }

                    //                    else if ( StringUtils.equalsIgnoreCase( strLineId, CONSTANT_GROUP ) )
                    //                    {
                    //                    	String strGroup = strValue.substring( nSeparatorIndex + 1 );
                    //                    	Group group = GroupHome.findByPrimaryKey(strGroup);
                    //                    	if(group == null) {
                    //                        	Object[] args = { strGroup };
                    //                            String strErrorMessage = I18nService.getLocalizedString( MESSAGE_GROUP_UNKNOWN, args, locale );
                    //                            CSVMessageDescriptor error = new CSVMessageDescriptor( CSVMessageLevel.ERROR, nLineNumber, strErrorMessage );
                    //                            listMessages.add( error );
                    //                    	}
                    //                    	else{
                    //                    		listGroups.add( strGroup );
                    //                    	}
                    //                    }
                    else
                    {
                        String strProfil = strValue.substring( nSeparatorIndex + 1 );
                        WssoProfil profil = WssoProfilHome.findWssoProfilByCode( strProfil, databasePlugin );

                        if ( profil == null )
                        {
                            Object[] args = { strProfil };
                            String strErrorMessage = I18nService.getLocalizedString( MESSAGE_PROFIL_UNKNOWN, args,
                                    locale );
                            CSVMessageDescriptor error = new CSVMessageDescriptor( CSVMessageLevel.ERROR, nLineNumber,
                                    strErrorMessage );
                            listMessages.add( error );
                        }
                        else
                        {
                            listProfils.add( strProfil );
                        }
                    }
                }
            }

            nIndex++;
        }

        // We create roles
        if ( CollectionUtils.isNotEmpty( listRoles ) )
        {
            for ( String strRole : listRoles )
            {
                WssoUserRoleHome.createRoleForUser( wssoUser.getMyluteceWssoUserId( ), strRole, databasePlugin );
            }
        }

        // We create groups
        //        for ( String strGoup : listGroups )
        //        {
        //            DatabaseHome.addGroupForUser( user.getUserId( ), strGoup, databasePlugin );
        //        }
        if ( CollectionUtils.isNotEmpty( listProfils ) )
        {
            for ( String profil : listProfils )
            {
                IdxWSSODatabaseHome.addUserForProfil( wssoUser.getMyluteceWssoUserId( ), profil, databasePlugin );
            }
        }

        return listMessages;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<CSVMessageDescriptor> checkLineOfCSVFile( String[] strLineDataArray, int nLineNumber, Locale locale )
    {
        int nMinColumnNumber = CONSTANT_MINIMUM_COLUMNS_PER_LINE;
        Plugin databasePlugin = PluginService.getPlugin( WssoDatabasePlugin.PLUGIN_NAME );
        List<CSVMessageDescriptor> listMessages = new ArrayList<CSVMessageDescriptor>( );

        if ( ( strLineDataArray == null ) || ( strLineDataArray.length < nMinColumnNumber ) )
        {
            int nNbCol;

            if ( strLineDataArray == null )
            {
                nNbCol = 0;
            }
            else
            {
                nNbCol = strLineDataArray.length;
            }

            Object[] args = { nNbCol, nMinColumnNumber };
            String strErrorMessage = I18nService.getLocalizedString( MESSAGE_ERROR_MIN_NUMBER_COLUMNS, args, locale );
            CSVMessageDescriptor error = new CSVMessageDescriptor( CSVMessageLevel.ERROR, nLineNumber, strErrorMessage );
            listMessages.add( error );

            return listMessages;
        }

        if ( !getUpdateExistingUsers( ) )
        {
            String strAccessCode = strLineDataArray[0];
            String strEmail = strLineDataArray[3];

            if ( WssoUserHome.findDatabaseUserIdFromGuid( strAccessCode, databasePlugin ) > 0 )
            {
                String strMessage = I18nService.getLocalizedString( MESSAGE_ACCESS_CODE_ALREADY_USED, locale );
                CSVMessageDescriptor error = new CSVMessageDescriptor( CSVMessageLevel.ERROR, nLineNumber, strMessage );
                listMessages.add( error );
            }
            else
            {
                Collection<WssoUser> listUsers = WssoUserHome.findWssoUserssByLastNameOrFirtNameOrEmailByProfil( null,
                        null, null, strEmail, databasePlugin );

                if ( ( listUsers != null ) && ( listUsers.size( ) > 0 ) )
                {
                    String strMessage = I18nService.getLocalizedString( MESSAGE_EMAIL_ALREADY_USED, locale );
                    CSVMessageDescriptor error = new CSVMessageDescriptor( CSVMessageLevel.ERROR, nLineNumber,
                            strMessage );
                    listMessages.add( error );
                }
            }
        }

        return listMessages;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<CSVMessageDescriptor> getEndOfProcessMessages( int nNbLineParses, int nNbLinesWithoutErrors,
            Locale locale )
    {
        List<CSVMessageDescriptor> listMessages = new ArrayList<CSVMessageDescriptor>( );
        Object[] args = { nNbLineParses, nNbLinesWithoutErrors };
        String strMessageContent = I18nService.getLocalizedString( MESSAGE_USERS_IMPORTED, args, locale );
        CSVMessageDescriptor message = new CSVMessageDescriptor( CSVMessageLevel.INFO, 0, strMessageContent );
        listMessages.add( message );

        return listMessages;
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

    /**
     * Get the separator used for attributes of admin users.
     * @return The separator
     */
    public Character getAttributesSeparator( )
    {
        if ( _strAttributesSeparator == null )
        {
            _strAttributesSeparator = AppPropertiesService.getProperty( PROPERTY_IMPORT_EXPORT_USER_SEPARATOR,
                    CONSTANT_DEFAULT_IMPORT_EXPORT_USER_SEPARATOR ).charAt( 0 );
        }

        return _strAttributesSeparator;
    }

    /**
     * Get the update users flag
     * @return True if existing users should be updated, false if they should be
     *         ignored.
     */
    public boolean getUpdateExistingUsers( )
    {
        return _bUpdateExistingUsers;
    }

    /**
     * Set the update users flag
     * @param bUpdateExistingUsers True if existing users should be updated,
     *            false if they should be ignored.
     */
    public void setUpdateExistingUsers( boolean bUpdateExistingUsers )
    {
        this._bUpdateExistingUsers = bUpdateExistingUsers;
    }
}
