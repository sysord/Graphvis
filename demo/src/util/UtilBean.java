package util;

import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;

@ManagedBean
public class UtilBean {

	public String getBaseLink(){
		return FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();
	}
	
}
