#version 330 core

out vec4 out_color;

in vec2 pass_tex_coord;
in vec3 pass_frag_pos;
smooth in vec3 pass_normal;

uniform sampler2D texture_diffuse;

void main()
{    
	out_color.rgb = pass_normal;
	out_color.a = 1;
	out_color.r = -out_color.r;
}