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
package fr.paris.lutece.plugins.mylutece.modules.wssodatabase.authentication.business;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.spring.SpringContextService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;


/**
 * This class provides instances management methods (create, find, ...) for
 * WssoUser objects
 */
public final class WssoUserHome
{
    // Static variable pointed at the DAO instance
    private static IWssoUserDAO _dao = (IWssoUserDAO) SpringContextService
            .getBean( "mylutece-wssodatabase.wssoUserDAO" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private WssoUserHome( )
    {
    }

    /**
     * Creation of an instance of wssoUser
     * 
     * @param wssoUser The instance of the wssoUser which contains the
     *            informations to store
     * @param plugin The current plugin using this method
     * @return The instance of wssoUser which has been created with its primary
     *         key.
     */
    public static WssoUser create( WssoUser wssoUser, Plugin plugin )
    {
        _dao.insert( wssoUser, plugin );

        return wssoUser;
    }

    /**
     * Update of the wssoUser which is specified in parameter
     * 
     * @param wssoUser The instance of the wssoUser which contains the data to
     *            store
     * @param plugin The current plugin using this method
     * @return The instance of the wssoUser which has been updated
     */
    public static WssoUser update( WssoUser wssoUser, Plugin plugin )
    {
        _dao.store( wssoUser, plugin );

        return wssoUser;
    }

    /**
     * Remove the WssoUser whose identifier is specified in parameter
     * 
     * @param wssoUser The WssoUser object to remove
     * @param plugin The current plugin using this method
     */
    public static void remove( WssoUser wssoUser, Plugin plugin )
    {
        _dao.delete( wssoUser, plugin );
    }

    // /////////////////////////////////////////////////////////////////////////
    // Finders

    /**
     * Returns an instance of a wssoUser whose identifier is specified in
     * parameter
     * 
     * @param nKey The Primary key of the wssoUser
     * @param plugin The current plugin using this method
     * @return An instance of wssoUser
     */
    public static WssoUser findByPrimaryKey( int nKey, Plugin plugin )
    {
        return _dao.load( nKey, plugin );
    }

    /**
     * Returns a collection of wssoUsers objects
     * @param plugin The current plugin using this method
     * @return A collection of wssoUsers
     */
    public static Collection<WssoUser> findWssoUsersList( Plugin plugin )
    {
        return _dao.selectWssoUserList( plugin );
    }

    /**
     * Returns a collection of wssoUsers objects for a role
     * @param nIdRole The role id of the wssoUser
     * @param plugin The current plugin using this method
     * @return A collection of wssoUsers
     */
    public static Collection findWssoUsersListForRole( int nIdRole, Plugin plugin )
    {
        return _dao.selectWssoUsersListForRole( nIdRole, plugin );
    }

    /**
     * Returns a collection of wssoUsers objects for a guid
     * @param strGuid The guid of the wssoUser
     * @param plugin The current plugin using this method
     * @return A collection of wssoUsers
     */
    public static Collection findWssoUsersListForGuid( String strGuid, Plugin plugin )
    {
        return _dao.selectWssoUserListForGuid( strGuid, plugin );
    }

    /**
     * Load the list of wssoUsers for a last name or first name and profil
     * 
     * @param strLastName The guid of WssoUser
     * @param codeProfil The Plugin using this data access service
     * @param strFirstName the first name
     * @param strEmail the email
     * @param plugin the current plugin
     * @return The Collection of the WssoUsers
     */
    public static List<WssoUser> findWssoUserssByLastNameOrFirtNameOrEmailByProfil( String codeProfil,
            String strLastName, String strFirstName, String strEmail, Plugin plugin )
    {
        return _dao.findWssoUserssByLastNameOrFirtNameOrEmailByProfil( codeProfil, strLastName, strFirstName, strEmail,
                plugin );
    }

    /**
     * Load the list of wssoUsers for a last name or first name and profil
     * 
     * @param strLastName The guid of WssoUser
     * @param codeProfil The Plugin using this data access service
     * @param strFirstName the first name
     * @param strEmail the email
     * @param plugin the current plugin
     * @return The Collection of the WssoUsers
     */
    public static List<WssoUser> findWssoUsersByLastNameOrFirstNameOrEmailByProfil( String strLastName,
            String strFirstName, String strEmail, Plugin plugin )
    {
        return _dao.findWssoUsersByLastNameOrFirstNameOrEmailByProfil( strLastName, strFirstName, strEmail, plugin );
    }

    /**
     * Get a user id from his guid
     * @param strGuid The guid of the user
     * @param plugin The plugin
     * @return The user id, or 0 if no user has this login.
     */
    public static int findDatabaseUserIdFromGuid( String strGuid, Plugin plugin )
    {
        return _dao.findDatabaseUserIdFromGuid( strGuid, plugin );
    }

    /**
     * Returns a collection of wssoUsers ids with a role
     * 
     * @param strRole
     *            The role of the wssoUser
     * @param plugin
     *            The current plugin using this method
     * @return A collection of wssoUsers ids
     */
    public static List<Integer> findWssoUserIdsListForRole( String strRole, Plugin plugin )
    {
        return _dao.selectWssoUserIdsListForRole( strRole, plugin );
    }

    /**
     * Returns a collection of roles by profils for user
     * @param nWssoUserId the user id
     * @param plugin The current plugin using this method
     * @return the list of roles for a user
     */
    public static List<String> findRolesByProfilsForUser( int nWssoUserId, Plugin plugin )
    {
        List<String> listProfils = WssoProfilHome.findWssoProfilsForUser( nWssoUserId, plugin );
        List<String> listRoles = new ArrayList<String>( );

        if ( CollectionUtils.isNotEmpty( listProfils ) )
        {
            for ( String codeProfil : listProfils )
            {
                List<String> profilRoleKeyList = IdxWSSODatabaseHome.findRolesFromProfil( codeProfil, plugin );

                if ( CollectionUtils.isNotEmpty( profilRoleKeyList ) )
                {
                    for ( String role : profilRoleKeyList )
                    {
                        if ( StringUtils.isNotBlank( role ) && !listRoles.contains( role ) )
                        {
                            listRoles.add( role );
                        }
                    }
                }
            }
        }

        return listRoles;
    }
}
