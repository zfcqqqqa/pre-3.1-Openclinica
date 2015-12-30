package org.akaza.openclinica.web.control.submit;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.MappingSqlQuery;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.GrantedAuthorityImpl;
import org.springframework.security.userdetails.User;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.security.userdetails.jdbc.JdbcDaoImpl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import javax.sql.DataSource;
public class OpenClinicaJdbcServiceStub extends JdbcDaoImpl {

    private MappingSqlQuery ocUsersByUsernameMapping,ocAuthoritiesByUsernameMapping;

    /**
     * Executes the <tt>usersByUsernameQuery</tt> and returns a list of UserDetails objects (there should normally only be one matching user).
     */
    @SuppressWarnings("unchecked")
    @Override
    protected List loadUsersByUsername(String username) {
        this.ocUsersByUsernameMapping = new OcUsersByUsernameMapping(getDataSource());
        return ocUsersByUsernameMapping.execute(username);
    }
    protected List loadUserAuthorities(String username) {
    	         
    	
    	this.ocAuthoritiesByUsernameMapping = new OCAuthoritiesByUsernameMapping(getDataSource());
    	return ocAuthoritiesByUsernameMapping.execute(username);
    	     }

    /**
     * Can be overridden to customize the creation of the final UserDetailsObject returnd from <tt>loadUserByUsername</tt>.
     * 
     * @param username
     *            the name originally passed to loadUserByUsername
     * @param userFromUserQuery
     *            the object returned from the execution of the
     * @param combinedAuthorities
     *            the combined array of authorities from all the authority loading queries.
     * @return the final UserDetails which should be used in the system.
     */
    @Override
    protected UserDetails createUserDetails(String username, UserDetails userFromUserQuery, GrantedAuthority[] combinedAuthorities) {
        String returnUsername = userFromUserQuery.getUsername();

        if (!isUsernameBasedPrimaryKey()) {
            returnUsername = username;
        }

        return new User(returnUsername, userFromUserQuery.getPassword(), userFromUserQuery.isEnabled(), true, true, userFromUserQuery.isAccountNonLocked(),
                combinedAuthorities);
    }

    /**
     * Query object to look up a user.
     */
    private class OcUsersByUsernameMapping extends MappingSqlQuery {
        protected OcUsersByUsernameMapping(DataSource ds) {
            super(ds, getUsersByUsernameQuery());
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        }

        @Override
        protected Object mapRow(ResultSet rs, int rownum) throws SQLException {
            String username = rs.getString(1);
            String password = rs.getString(2);
            boolean enabled = rs.getBoolean(3);
            boolean nonLocked = rs.getBoolean(4);
            UserDetails user = new User(username, password, enabled, true, true, nonLocked, new GrantedAuthority[] { new GrantedAuthorityImpl("HOLDER") });

            return user;
        }
    }
    
    private class OCAuthoritiesByUsernameMapping extends MappingSqlQuery {
    	         protected OCAuthoritiesByUsernameMapping(DataSource ds) {
    	             super(ds, getAuthoritiesByUsernameQuery());
    	             declareParameter(new SqlParameter(Types.VARCHAR));
    	             compile();
    	         }
    	 
    	         protected Object mapRow(ResultSet rs, int rownum) throws SQLException {
    	             String roleName = getRolePrefix() + rs.getString(2);
    	             GrantedAuthorityImpl authority = new GrantedAuthorityImpl(roleName);
    	 
    	             return authority;
    	         }
    	     }

}
