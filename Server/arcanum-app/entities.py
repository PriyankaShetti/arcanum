from google.appengine.ext import ndb
import json

#
# Class to enable json serialization
class DictModel(ndb.Model):
    # Obsolet with NDB.
    #def to_dict(self):
    #    return dict([(p, unicode(getattr(self, p))) for p in self.properties()])
    def to_json(self):
        return json.dumps(self.to_dict())

class RawUser(DictModel):
    lookup_key  = ndb.StringProperty(required=True)
    phones      = ndb.StringProperty(repeated=True)
    
    def parse(self, dct):
        self.lookup_key = dct.get("lookup_key")
        self.phones     = dct.get("phones")

class User(DictModel):
    hash = ndb.StringProperty(required=True) # Hashed Phone Number
    type = ndb.StringProperty(required=True) # Phone type (Android, iOS, Win8, ...)
    public_key = ndb.TextProperty(required=True) # Public Key (PEM-Format)
    registration_ids = ndb.StringProperty(repeated=True,indexed=False) # IDs for Push Notifications
    created = ndb.DateTimeProperty(required=True,indexed=False,auto_now_add=True)
    modified = ndb.DateTimeProperty(required=True,indexed=False,auto_now=True)

    def parse(self, dictionary):
        self.hash = dictionary.get("hash")
        self.type = dictionary.get("type")
        self.public_key = dictionary.get("public_key")
        self.registration_ids = dictionary.get("registration_ids")
        self.created = dictionary.get("created")
        self.modified = dictionary.get("modified")

    def isValid(self):
        if self.hash is None:
            return False
        if self.type is None:
            return False
        if self.public_key is None:
            return False
        if self.registration_ids is None:
            return False
        return True

    def isUnique(self):
        usr_query = User.query(User.hash == self.hash, User.type == self.type)
        isInDatastore = bool(usr_query.count(limit=1))
        return not isInDatastore
    
    def loadme(self):
        usr_query = User.query(User.hash == self.hash)
        if self.type is not None:
            usr_query = usr_query.filter(User.type == self.type)
        return usr_query.get()

class RawMessage(DictModel):
    content = ndb.BlobProperty(required=True,indexed=False)        
    
class Message(DictModel):
    sender      = ndb.StringProperty(required=True)
    recipient   = ndb.StringProperty(required=True)
    iv          = ndb.BlobProperty(required=True,indexed=False)
    secretkey   = ndb.BlobProperty(required=True,indexed=False)
    content     = ndb.BlobProperty(required=True,indexed=False)
    contentType = ndb.StringProperty(indexed=False)
    created = ndb.DateTimeProperty(required=True,indexed=False,auto_now_add=True)
    pushed = ndb.DateTimeProperty(indexed=False)
    readed = ndb.DateTimeProperty(indexed=False)
    modified = ndb.DateTimeProperty(required=True,indexed=False,auto_now=True)
    
    def parse(self, dictionary):
        self.sender = dictionary.get("sender")
        self.recipient = dictionary.get("recipient")
        self.content = dictionary.get("content")
        self.contentType = dictionary.get("contentType")

    def isValid(self):
        if self.sender is None:
            return False
        if self.recipient is None:
            return False
        if self.content is None:
            return False
        if self.contentType is None:
            return False
        return True
    
