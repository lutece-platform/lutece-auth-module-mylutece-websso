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

import fr.paris.lutece.plugins.mylutece.authentication.MultiLuteceAuthentication;
import fr.paris.lutece.plugins.mylutece.modules.wssodatabase.authentication.IdxWSSODatabaseAuthentication;
import fr.paris.lutece.plugins.mylutece.modules.wssodatabase.authentication.business.WssoProfilHome;
import fr.paris.lutece.plugins.mylutece.modules.wssodatabase.authentication.business.WssoUser;
import fr.paris.lutece.plugins.mylutece.modules.wssodatabase.authentication.business.WssoUserRoleHome;
import fr.paris.lutece.plugins.mylutece.modules.wssodatabase.authentication.business.WssoUserWrapper;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppLogService;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.collections.CollectionUtils;


/**
 * 
 * LdapDatabaseService
 * 
 */
public class WssoDatabaseService
{
    private static final String AUTHENTICATION_BEAN_NAME = "mylutece-wssodatabase.authentication";

    private static WssoDatabaseService _singleton = new WssoDatabaseService( );

    /**
     * Initialize the WssoDatabase service
     * 
     */
    public void init( )
    {
        WssoUser.init( );

        IdxWSSODatabaseAuthentication baseAuthentication = (IdxWSSODatabaseAuthentication) SpringContextService
                .getPluginBean( WssoDatabasePlugin.PLUGIN_NAME, AUTHENTICATION_BEAN_NAME );

        if ( baseAuthentication != null )
        {
            MultiLuteceAuthentication.registerAuthentication( baseAuthentication );
        }
        else
        {
            AppLogService
                    .error( "IdxWSSODatabaseAuthentication not found, please check your wssodatabase_context.xml configuration" );
        }
    }

    /**
     * Returns the instance of the singleton
     * 
     * @return The instance of the singleton
     */
    public static WssoDatabaseService getInstance( )
    {
        return _singleton;
    }

    /**
     * Get a WssoUserWrapper describing a given user for export
     */
    public WssoUserWrapper getExportUser( WssoUser user, boolean bExportRoles, boolean bExportProfils )
    {
        Plugin databasePlugin = PluginService.getPlugin( WssoDatabasePlugin.PLUGIN_NAME );
        WssoUserWrapper wssoUserExport = new WssoUserWrapper( user );

        if ( bExportRoles )
        {
            Collection<String> userRoleList = WssoUserRoleHome.findRolesListForUser( user.getMyluteceWssoUserId( ), databasePlugin );
            if ( CollectionUtils.isNotEmpty( userRoleList ) )
            {
                wssoUserExport.setRoles( new ArrayList<>( userRoleList ) );
            }
        }

        if ( bExportProfils )
        {
            Collection<String> userProfilList = WssoProfilHome.findWssoProfilsForUser( user.getMyluteceWssoUserId( ), databasePlugin );
            if ( CollectionUtils.isNotEmpty( userProfilList ) )
            {
                wssoUserExport.setProfils( new ArrayList<>( userProfilList ) );
            }
        }

        return wssoUserExport;
    }
}
