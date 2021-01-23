# Creating a new release

The following is an example of creating a new release via the CircleCI job API. 
Refer to the [Authentication](https://circleci.com/docs/api/#authentication) section to
see how the `authorization` header should be populated. 

```
curl --request POST \
  --url https://circleci.com/api/v1.1/project/github/itzg/kidsbank-js/tree/master \
  --header 'authorization: Basic ...' \
  --header 'content-type: application/json' \
  --data '{
	"build_parameters": {
		"CIRCLE_JOB": "release",
		"RELEASE": "0.4.0",
		"NEXT": "0.5-SNAPSHOT"
	}
}'
```