package fr.paris.lutece.plugins.mylutece.modules.wssodatabase.authentication.business;

import org.apache.commons.lang3.StringUtils;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;

/**
 * This class represents the business object WssoUserWrapper for export
 */
public class WssoUserWrapper implements Serializable
{
    private final WssoUser _wssoUser;

    private List<String> _roles;
    private List<String> _profils;

    public WssoUserWrapper( WssoUser wssoUser )
    {
        _wssoUser = wssoUser;
    }

    public String getGuid( )
    {
        return _wssoUser.getGuid( );
    }

    public String getLastName( )
    {
        return _wssoUser.getLastName( );
    }

    public String getFirstName( )
    {
        return _wssoUser.getFirstName( );
    }

    public String getEmail( )
    {
        return _wssoUser.getEmail( );
    }

    public List<String> getRoles( )
    {
        return _roles;
    }

    public void setRoles( List<String> roles )
    {
        this._roles = roles;
    }

    public List<String> getProfils( )
    {
        return _profils;
    }

    public void setProfils( List<String> profils )
    {
        this._profils = profils;
    }
}
