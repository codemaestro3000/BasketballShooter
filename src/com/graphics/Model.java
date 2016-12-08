package com.graphics;

import java.util.ArrayList;

import com.viduus.charon.global.graphics.opengl.OpenGLGraphics;
import com.viduus.charon.global.graphics.opengl.OpenGLRenderable;
import com.viduus.util.models.ModelData;

public class Model implements OpenGLRenderable{

	private final ModelData model_data;
	private ArrayList<RenderableMesh> meshes;
	
	/**
	 * Creates a new Model.
	 * @param model_name - The identifier for this model
	 * @param version - Collada version
	 * @param object_meshes - The mesh of vertices, normals, and texture points
	 */
	public Model(ModelData model_data) {
		this.model_data = model_data;
		this.meshes = new ArrayList<RenderableMesh>();
		
		for( com.viduus.util.models.geometries.Mesh m : model_data.mesh.values() ){
			meshes.add(new RenderableMesh(m));
		}
	}

	@Override
	public void render(OpenGLGraphics graphics) {
//		graphics.model_matrix.rotate(1, 0, 0, (float) Math.PI/2);
		graphics.pushMatricies();
		
		for( RenderableMesh m : this.meshes){
//			if(m.getName().compareTo("Goof_Head_Ambi") == 0)
			m.render( graphics );
		}
		
		graphics.model_matrix.reset();
	}
}
