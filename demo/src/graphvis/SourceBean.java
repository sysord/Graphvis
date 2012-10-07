package graphvis;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.faces.bean.ManagedBean;

@ManagedBean
public class SourceBean {

	public String getSource(String sourceName){
		InputStream is = null;
		
		try {
			is = this.getClass().getResourceAsStream("/conf/" + sourceName);
			BufferedReader bfr = new BufferedReader(new InputStreamReader(is));
			StringBuffer fileContent = new StringBuffer();
			String line;
			while((line = bfr.readLine()) != null){
				fileContent.append(line);
				fileContent.append('\n');
			}
			return fileContent.toString();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "ERREUR "  + e.getMessage();
		} finally{
			if(is != null){
				try {is.close();} catch (IOException e) {}
			}
		}
		
	}

}
