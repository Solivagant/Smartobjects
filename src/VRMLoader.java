import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URL;

import javax.media.j3d.BranchGroup;

import org.jdesktop.j3d.loaders.vrml97.VrmlLoader;
import com.sun.j3d.loaders.Scene;

import vrml.node.Node;

public class VRMLoader {

	static BranchGroup load(File f) throws IOException {
		BranchGroup out = null;

	
		
		String filename = f.getAbsolutePath();
		File basedir = f.getParentFile();
		URI baseurl = basedir.toURI(); 
		
		System.out.println("Loading VRML Object " + filename
				+ ". Path is: " + filename);

		Scene scene = null;

		// Xj3D Loader
		VrmlLoader vrmlloader = new VrmlLoader(VrmlLoader.LOAD_ALL);
		vrmlloader.setBasePath(filename);
		vrmlloader.setBaseUrl(baseurl.toURL());
		try {
			FileReader r = new FileReader(f);
			scene = vrmlloader.load(r);
		} catch (java.io.FileNotFoundException e) {
			System.err.println(e.toString());
			throw e;
		}

		if (scene != null)
			out = scene.getSceneGroup();
		else {
			throw new IOException("Scene results null loading file '"
					+ filename + "'");
		}

		return out;

	}

}
