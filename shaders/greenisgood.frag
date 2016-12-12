#version 330 core

// player information
uniform vec3 eye_loc;

// light information
uniform vec3 light_dir;

// mesh information
uniform vec4 emission;
uniform vec4 ambient;
uniform vec4 diffuse;
uniform vec4 specular;
uniform vec4 reflective;
uniform vec4 transparent;

uniform float shininess;
uniform float reflectivity;
uniform float transparency;
uniform float index_of_refraction;

uniform int using_texture = 0;
uniform sampler2D texture_diffuse;

// inputs
in vec2 pass_tex_coord;
in vec3 pass_frag_pos;
in vec3 pass_normal;

// outputs
out vec4 out_color;

void main()
{
	vec3 diffuse_color = diffuse.rgb;
	if( using_texture != 0 ){
		diffuse_color = texture( texture_diffuse, pass_tex_coord ).rgb;
	}

	out_color = vec4(0);
	
	// calculate ambient contribution

	// calculate diffuse contribution
	float angle = dot(pass_normal, light_dir);
	out_color.rgb += diffuse_color.rgb * angle;
	
	// calculate specular contribution
//	vec3 reflected_light_ray_dir = pass_normal * 2 * (light_dir * pass_normal) - light_dir;
//	vec3 eye_dir = normalize(eye_loc - pass_frag_pos);
//	angle = dot(reflected_light_ray_dir, eye_dir);
//	out_color.rgb += specular.rgb * pow(angle, shininess);
	
	out_color.a = 1;
}