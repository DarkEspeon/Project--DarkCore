#version 400 core

in vec3 position;
in vec2 uv;
in vec3 norm;

out vec2 pass_textureCoords;
out vec3 surfaceNormal;
out vec3 toLightVector;
out vec3 toCameraVector;
out float visibility;

uniform mat4 projMat;
uniform mat4 transMat;
uniform mat4 viewMat;
uniform vec3 lightPosition;

const float density = 0.007;
const float gradient = 1.5;

void main(void){
	vec4 worldPos = transMat * vec4(position, 1.0);
	vec4 posRelativeToCam = viewMat * worldPos;
	gl_Position = projMat * posRelativeToCam;
	pass_textureCoords = uv;
	
	surfaceNormal = (transMat * vec4(norm, 0.0)).xyz;
	toLightVector = lightPosition - worldPos.xyz;
	toCameraVector = (inverse(viewMat) * vec4(0, 0, 0, 1)).xyz - worldPos.xyz;
	
	float distance = length(posRelativeToCam.xyz);
	visibility = exp(-pow((distance * density), gradient));
	visibility = clamp(visibility, 0.0, 1.0);
}