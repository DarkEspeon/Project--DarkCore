#version 330

in vec3 pos;
in vec2 texCoord;
in vec3 norm;

uniform mat4 viewMat;
uniform mat4 transMat;
uniform mat4 projMat;

out vec3 pos0;
out vec2 texCoord0;
out vec3 norm0;

void main(){
	texCoord0 = texCoord;
	norm0 = normalize((transMat * vec4(norm, 0.0)).xyz);
	pos0 = (transMat * vec4(pos, 1.0)).xyz;
	gl_Position = projMat * viewMat * transMat * vec4(pos, 1.0);
}