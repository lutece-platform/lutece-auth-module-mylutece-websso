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
import fr.paris.lutece.util.sql.DAOUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;


/**
 * This class provides Data Access methods for WssoUser objects
 */
public final class WssoUserDAO implements IWssoUserDAO
{
    // Constants
    private static final String SQL_QUERY_NEW_PK = " SELECT max( mylutece_wsso_user_id ) FROM mylutece_wsso_user ";
    private static final String SQL_QUERY_SELECT = " SELECT mylutece_wsso_user_id, guid, last_name, first_name, email, date_last_login FROM mylutece_wsso_user WHERE mylutece_wsso_user_id = ? ";
    private static final String SQL_QUERY_INSERT = " INSERT INTO mylutece_wsso_user ( mylutece_wsso_user_id, guid, last_name, first_name, email ) VALUES ( ?, ?, ?, ?, ? ) ";
    private static final String SQL_QUERY_DELETE = " DELETE FROM mylutece_wsso_user WHERE mylutece_wsso_user_id = ?  ";
    private static final String SQL_QUERY_UPDATE = " UPDATE mylutece_wsso_user SET mylutece_wsso_user_id = ?, guid = ?, last_name = ?, first_name = ?, email = ? WHERE mylutece_wsso_user_id = ?  ";
    private static final String SQL_QUERY_SELECTALL = " SELECT mylutece_wsso_user_id, guid, last_name, first_name, email, date_last_login FROM mylutece_wsso_user ORDER BY last_name, first_name, email ";
    private static final String SQL_QUERY_SELECTALL_FOR_ROLE = " SELECT u.mylutece_wsso_user_id, u.guid, u.last_name, u.first_name, u.email, u.date_last_login FROM mylutece_wsso_user u, mylutece_wsso_user_role ur WHERE u.mylutece_wsso_user_id = ur.mylutece_wsso_user_id AND ur.mylutece_wsso_role_id = ? ORDER BY u.last_name, u.first_name, u.email ";
    private static final String SQL_QUERY_SELECTALL_FOR_GUID = " SELECT mylutece_wsso_user_id, guid, last_name, first_name, email, date_last_login FROM mylutece_wsso_user WHERE guid = ? ORDER BY last_name, first_name, email ";
    private static final String SQL_SELECT_WSSO_USER_ID_FROM_GUID = "SELECT mylutece_wsso_user_id FROM mylutece_wsso_user WHERE guid = ?";
    private static final String SQL_QUERY_SELECT_WSSO_USER_IDS_WITH_ROLE = "SELECT distinct u.mylutece_wsso_user_id FROM mylutece_wsso_user u, mylutece_wsso_user_role ur WHERE u.mylutece_wsso_user_id = ur.mylutece_wsso_user_id AND ur.role = ?";

    /** This class implements the Singleton design pattern. */
    private static WssoUserDAO _dao = new WssoUserDAO( );

    /**
     * Creates a new WssoUserDAO object.
     */
    private WssoUserDAO( )
    {
    }

    /**
     * Returns the unique instance of the singleton.
     * 
     * @return the instance
     */
    static WssoUserDAO getInstance( )
    {
        return _dao;
    }

    /**
     * Generates a new primary key
     * @param plugin The Plugin using this data access service
     * @return The new primary key
     */
    public int newPrimaryKey( Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_NEW_PK, plugin );
        daoUtil.executeQuery( );

        int nKey;

        if ( !daoUtil.next( ) )
        {
            // if the table is empty
            nKey = 1;
        }

        nKey = daoUtil.getInt( 1 ) + 1;

        daoUtil.free( );

        return nKey;
    }

    /**
     * Insert a new record in the table.
     * 
     * @param wssoUser The wssoUser object
     * @param plugin The Plugin using this data access service
     */
    public void insert( WssoUser wssoUser, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );
        wssoUser.setMyluteceWssoUserId( newPrimaryKey( plugin ) );
        daoUtil.setInt( 1, wssoUser.getMyluteceWssoUserId( ) );
        daoUtil.setString( 2, wssoUser.getGuid( ) );
        daoUtil.setString( 3, wssoUser.getLastName( ) );
        daoUtil.setString( 4, wssoUser.getFirstName( ) );
        daoUtil.setString( 5, wssoUser.getEmail( ) );

        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * Load the data of WssoUser from the table
     * 
     * @param nWssoUserId The identifier of WssoUser
     * @param plugin The Plugin using this data access service
     * @return the instance of the WssoUser
     */
    public WssoUser load( int nWssoUserId, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin );
        daoUtil.setInt( 1, nWssoUserId );
        daoUtil.executeQuery( );

        WssoUser wssoUser = null;

        if ( daoUtil.next( ) )
        {
            wssoUser = new WssoUser( );
            wssoUser.setMyluteceWssoUserId( daoUtil.getInt( 1 ) );
            wssoUser.setGuid( daoUtil.getString( 2 ) );
            wssoUser.setLastName( daoUtil.getString( 3 ) );
            wssoUser.setFirstName( daoUtil.getString( 4 ) );
            wssoUser.setEmail( daoUtil.getString( 5 ) );
            wssoUser.setDateLastLogin( daoUtil.getDate( 6 ) );
        }

        daoUtil.free( );

        return wssoUser;
    }

    /**
     * Delete a record from the table
     * @param wssoUser The WssoUser object
     * @param plugin The Plugin using this data access service
     */
    public void delete( WssoUser wssoUser, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin );
        daoUtil.setInt( 1, wssoUser.getMyluteceWssoUserId( ) );

        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * Update the record in the table
     * @param wssoUser The reference of wssoUser
     * @param plugin The Plugin using this data access service
     */
    public void store( WssoUser wssoUser, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin );
        daoUtil.setInt( 1, wssoUser.getMyluteceWssoUserId( ) );
        daoUtil.setString( 2, wssoUser.getGuid( ) );
        daoUtil.setString( 3, wssoUser.getLastName( ) );
        daoUtil.setString( 4, wssoUser.getFirstName( ) );
        daoUtil.setString( 5, wssoUser.getEmail( ) );
        daoUtil.setInt( 6, wssoUser.getMyluteceWssoUserId( ) );

        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * Load the list of wssoUsers
     * @param plugin The Plugin using this data access service
     * @return The Collection of the WssoUsers
     */
    public Collection<WssoUser> selectWssoUserList( Plugin plugin )
    {
        Collection<WssoUser> listWssoUsers = new ArrayList<WssoUser>( );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin );
        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            WssoUser wssoUser = new WssoUser( );
            wssoUser.setMyluteceWssoUserId( daoUtil.getInt( 1 ) );
            wssoUser.setGuid( daoUtil.getString( 2 ) );
            wssoUser.setLastName( daoUtil.getString( 3 ) );
            wssoUser.setFirstName( daoUtil.getString( 4 ) );
            wssoUser.setEmail( daoUtil.getString( 5 ) );
            wssoUser.setDateLastLogin( daoUtil.getDate( 6 ) );

            listWssoUsers.add( wssoUser );
        }

        daoUtil.free( );

        return listWssoUsers;
    }

    /**
     * Load the list of wssoUsers for a role
     * @param nIdRole The role of WssoUser
     * @param plugin The Plugin using this data access service
     * @return The Collection of the WssoUsers
     */
    public Collection<WssoUser> selectWssoUsersListForRole( int nIdRole, Plugin plugin )
    {
        Collection<WssoUser> listWssoUsers = new ArrayList<WssoUser>( );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_FOR_ROLE, plugin );
        daoUtil.setInt( 1, nIdRole );
        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            WssoUser wssoUser = new WssoUser( );
            wssoUser.setMyluteceWssoUserId( daoUtil.getInt( 1 ) );
            wssoUser.setGuid( daoUtil.getString( 2 ) );
            wssoUser.setLastName( daoUtil.getString( 3 ) );
            wssoUser.setFirstName( daoUtil.getString( 4 ) );
            wssoUser.setEmail( daoUtil.getString( 5 ) );
            wssoUser.setDateLastLogin( daoUtil.getDate( 6 ) );

            listWssoUsers.add( wssoUser );
        }

        daoUtil.free( );

        return listWssoUsers;
    }

    /**
     * Load the list of wssoUser id for a role
     * 
     * @param strRole
     *            The role of WssoUser
     * @param plugin
     *            The Plugin using this data access service
     * @return The Collection of the WssoUser ids
     */
    public List<Integer> selectWssoUserIdsListForRole( String strRole, Plugin plugin )
    {
        List<Integer> listWssoUserIds = new ArrayList<Integer>( );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_WSSO_USER_IDS_WITH_ROLE, plugin );
        daoUtil.setString( 1, strRole );
        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            listWssoUserIds.add( daoUtil.getInt( 1 ) );
        }

        daoUtil.free( );

        return listWssoUserIds;
    }

    /**
     * Load the list of wssoUsers for a guid
     * @param strGuid The guid of WssoUser
     * @param plugin The Plugin using this data access service
     * @return The Collection of the WssoUsers
     */
    public Collection<WssoUser> selectWssoUserListForGuid( String strGuid, Plugin plugin )
    {
        Collection<WssoUser> listWssoUsers = new ArrayList<WssoUser>( );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_FOR_GUID, plugin );
        daoUtil.setString( 1, strGuid );
        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            WssoUser wssoUser = new WssoUser( );
            wssoUser.setMyluteceWssoUserId( daoUtil.getInt( 1 ) );
            wssoUser.setGuid( daoUtil.getString( 2 ) );
            wssoUser.setLastName( daoUtil.getString( 3 ) );
            wssoUser.setFirstName( daoUtil.getString( 4 ) );
            wssoUser.setEmail( daoUtil.getString( 5 ) );
            wssoUser.setDateLastLogin( daoUtil.getDate( 6 ) );

            listWssoUsers.add( wssoUser );
        }

        daoUtil.free( );

        return listWssoUsers;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<WssoUser> findWssoUserssByLastNameOrFirtNameOrEmailByProfil( String codeProfil, String strLastName,
            String strFirstName, String strEmail, Plugin plugin )
    {
        List<WssoUser> listWssoUsers = new ArrayList<WssoUser>( );

        StringBuffer strSQL = new StringBuffer(
                "SELECT a.mylutece_wsso_user_id, a.guid, a.last_name, a.first_name, a.email, a.date_last_login FROM mylutece_wsso_user a INNER JOIN mylutece_wsso_profil_user b on a.mylutece_wsso_user_id = b.mylutece_wsso_user_id " );

        strSQL.append( "WHERE b.mylutece_wsso_profil_code = ? " );

        if ( StringUtils.isNotBlank( strLastName ) || StringUtils.isNotBlank( strFirstName )
                || StringUtils.isNotBlank( strEmail ) )
        {
            if ( StringUtils.isNotBlank( strLastName ) )
            {
                strSQL.append( "AND a.last_name = ? " );

                if ( StringUtils.isNotBlank( strFirstName ) )
                {
                    strSQL.append( "AND a.first_name = ? " );

                    if ( StringUtils.isNotBlank( strEmail ) )
                    {
                        strSQL.append( "AND a.email = ? " );
                    }
                }
                else if ( StringUtils.isNotBlank( strEmail ) )
                {
                    strSQL.append( "AND a.email = ? " );
                }
            }
            else
            {
                if ( StringUtils.isNotBlank( strFirstName ) )
                {
                    strSQL.append( "AND a.first_name = ? " );

                    if ( StringUtils.isNotBlank( strEmail ) )
                    {
                        strSQL.append( "AND a.email = ? " );
                    }
                }
                else if ( StringUtils.isNotBlank( strEmail ) )
                {
                    strSQL.append( "AND a.email = ? " );
                }
            }
        }

        DAOUtil daoUtil = new DAOUtil( strSQL.toString( ), plugin );

        daoUtil.setString( 1, codeProfil );

        if ( StringUtils.isNotBlank( strLastName ) || StringUtils.isNotBlank( strFirstName )
                || StringUtils.isNotBlank( strEmail ) )
        {
            if ( StringUtils.isNotBlank( strLastName ) )
            {
                daoUtil.setString( 2, strLastName );

                if ( StringUtils.isNotBlank( strFirstName ) )
                {
                    daoUtil.setString( 3, strFirstName );

                    if ( StringUtils.isNotBlank( strEmail ) )
                    {
                        daoUtil.setString( 4, strEmail );
                    }
                }
                else if ( StringUtils.isNotBlank( strEmail ) )
                {
                    daoUtil.setString( 3, strEmail );
                }
            }
            else
            {
                if ( StringUtils.isNotBlank( strFirstName ) )
                {
                    daoUtil.setString( 2, strFirstName );

                    if ( StringUtils.isNotBlank( strEmail ) )
                    {
                        daoUtil.setString( 3, strEmail );
                    }
                }
                else if ( StringUtils.isNotBlank( strEmail ) )
                {
                    daoUtil.setString( 2, strEmail );
                }
            }
        }

        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            WssoUser wssoUser = new WssoUser( );
            wssoUser.setMyluteceWssoUserId( daoUtil.getInt( 1 ) );
            wssoUser.setGuid( daoUtil.getString( 2 ) );
            wssoUser.setLastName( daoUtil.getString( 3 ) );
            wssoUser.setFirstName( daoUtil.getString( 4 ) );
            wssoUser.setEmail( daoUtil.getString( 5 ) );
            wssoUser.setDateLastLogin( daoUtil.getDate( 6 ) );

            listWssoUsers.add( wssoUser );
        }

        daoUtil.free( );

        return listWssoUsers;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<WssoUser> findWssoUsersByLastNameOrFirstNameOrEmailByProfil( String strLastName, String strFirstName,
            String strEmail, Plugin plugin )
    {
        List<WssoUser> listWssoUsers = new ArrayList<WssoUser>( );

        StringBuffer strSQL = new StringBuffer(
                "SELECT a.mylutece_wsso_user_id, a.guid, a.last_name, a.first_name, a.email, a.date_last_login FROM mylutece_wsso_user a " );

        if ( StringUtils.isNotBlank( strLastName ) || StringUtils.isNotBlank( strFirstName )
                || StringUtils.isNotBlank( strEmail ) )
        {
            strSQL.append( "WHERE " );
            if ( StringUtils.isNotBlank( strLastName ) )
            {
                strSQL.append( "LOWER(a.last_name) LIKE ? " );

                if ( StringUtils.isNotBlank( strFirstName ) )
                {
                    strSQL.append( "AND LOWER(a.first_name) LIKE ? " );

                    if ( StringUtils.isNotBlank( strEmail ) )
                    {
                        strSQL.append( "AND LOWER(a.email) LIKE ? " );
                    }
                }
                else if ( StringUtils.isNotBlank( strEmail ) )
                {
                    strSQL.append( "AND LOWER(a.email) LIKE ? " );
                }
            }
            else
            {
                if ( StringUtils.isNotBlank( strFirstName ) )
                {
                    strSQL.append( "LOWER(a.first_name) LIKE ? " );

                    if ( StringUtils.isNotBlank( strEmail ) )
                    {
                        strSQL.append( "AND LOWER(a.email) LIKE ? " );
                    }
                }
                else if ( StringUtils.isNotBlank( strEmail ) )
                {
                    strSQL.append( "LOWER(a.email) LIKE ? " );
                }
            }
        }

        DAOUtil daoUtil = new DAOUtil( strSQL.toString( ), plugin );

        if ( StringUtils.isNotBlank( strLastName ) || StringUtils.isNotBlank( strFirstName )
                || StringUtils.isNotBlank( strEmail ) )
        {
            if ( StringUtils.isNotBlank( strLastName ) )
            {
                daoUtil.setString( 1, "%" + strLastName.toLowerCase( ) + "%" );

                if ( StringUtils.isNotBlank( strFirstName ) )
                {
                    daoUtil.setString( 2, "%" + strFirstName.toLowerCase( ) + "%" );

                    if ( StringUtils.isNotBlank( strEmail ) )
                    {
                        daoUtil.setString( 3, "%" + strEmail.toLowerCase( ) + "%" );
                    }
                }
                else if ( StringUtils.isNotBlank( strEmail ) )
                {
                    daoUtil.setString( 2, "%" + strEmail.toLowerCase( ) + "%" );
                }
            }
            else
            {
                if ( StringUtils.isNotBlank( strFirstName ) )
                {
                    daoUtil.setString( 1, "%" + strFirstName.toLowerCase( ) + "%" );

                    if ( StringUtils.isNotBlank( strEmail ) )
                    {
                        daoUtil.setString( 2, "%" + strEmail.toLowerCase( ) + "%" );
                    }
                }
                else if ( StringUtils.isNotBlank( strEmail ) )
                {
                    daoUtil.setString( 1, "%" + strEmail.toLowerCase( ) + "%" );
                }
            }
        }

        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            WssoUser wssoUser = new WssoUser( );
            wssoUser.setMyluteceWssoUserId( daoUtil.getInt( 1 ) );
            wssoUser.setGuid( daoUtil.getString( 2 ) );
            wssoUser.setLastName( daoUtil.getString( 3 ) );
            wssoUser.setFirstName( daoUtil.getString( 4 ) );
            wssoUser.setEmail( daoUtil.getString( 5 ) );
            wssoUser.setDateLastLogin( daoUtil.getDate( 6 ) );

            listWssoUsers.add( wssoUser );
        }

        daoUtil.free( );

        return listWssoUsers;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int findDatabaseUserIdFromGuid( String strGuid, Plugin plugin )
    {
        int nRecordId = 0;

        DAOUtil daoUtil = new DAOUtil( SQL_SELECT_WSSO_USER_ID_FROM_GUID, plugin );
        daoUtil.setString( 1, strGuid );
        daoUtil.executeQuery( );

        if ( daoUtil.next( ) )
        {
            nRecordId = daoUtil.getInt( 1 );
        }

        daoUtil.free( );

        return nRecordId;
    }
}
