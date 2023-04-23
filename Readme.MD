### EntityGenerator
- will generate the Jar Based on a config file for the Entity

### CRUD
- Will expose the Basic crud operations
- WIll allow an RDM service to be registered
- Will have some in-memory config on which RDM calls to make so that we can avoid calling (RDM transcoding) for all attribute values
- Will have additional logic for get operations to call RDM lookup values and then send those values to client
- Storing will happen for original data and transcoding will happen on the fly

### RDM
- will have a underlying No_SQL db and a cache(Redis) sitting on top so that calls are blazing fast
- The cache will be refreshed every x minutes
- will have a get/post api for RDM `entityNameInConfig/AttributeNameInConfig`
