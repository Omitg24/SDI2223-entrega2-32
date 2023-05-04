module.exports = {
    mongoClient: null,
    app: null,
    init: function (app, mongoClient) {
        this.mongoClient = mongoClient;
        this.app = app;
    },
    findConversation: async function (filter, options) {
        try {
            console.log("a1");
            const client = await this.mongoClient.connect(this.app.get('connectionStrings'));
            console.log("a2");
            const database = client.db("UrWalletPop");
            console.log("a3");
            const collectionName = 'conversations';
            const conversationsCollection = database.collection(collectionName);
            console.log("a4");
            let conversation=null;
            console.log("aasdassa");
            try{
                conversation = await conversationsCollection.findOne(filter, options);
            }catch(error){}
            return conversation;
        } catch (error) {
            throw (error);
        }
    },
    getConversations: async function (filter, options) {
        try {
            const client = await this.mongoClient.connect(this.app.get('connectionStrings'));
            const database = client.db("UrWalletPop");
            const collectionName = 'conversations';
            const conversationsCollection = database.collection(collectionName);
            const conversations = await conversationsCollection.find(filter, options).toArray();
            return conversations;
        } catch (error) {
            throw (error);
        }
    },
    getUsersPg: async function (filter, options, page) {
        try {
            const limit = 4;
            const client = await this.mongoClient.connect(this.app.get('connectionStrings'));
            const database = client.db("UrWalletPop");
            const collectionName = 'users';
            const usersCollection = database.collection(collectionName);
            const usersCollectionCount = await usersCollection.count();
            const cursor = usersCollection.find(filter, options).skip((page - 1) * limit).limit(limit)
            const users = await cursor.toArray();
            const result = {users: users, total: usersCollectionCount};
            return result;
        } catch (error) {
            throw (error);
        }
    },
    insertConversation: async function (conversation) {
        try {
            const client = await this.mongoClient.connect(this.app.get('connectionStrings'));
            const database = client.db("UrWalletPop");
            const collectionName = 'conversations';
            const conversationsCollection = database.collection(collectionName);
            const result = await conversationsCollection.insertOne(conversation);
            return result.insertedId;
        } catch (error) {
            throw (error);
        }
    },
    updateConversation: async function(newConversation, filter, options) {
        try {
            const client = await this.mongoClient.connect(this.app.get('connectionStrings'));
            const database = client.db("UrWalletPop");
            const collectionName = 'conversations';
            const conversationsCollection = database.collection(collectionName);
            const result = await conversationsCollection.updateOne(filter, {$set: newConversation}, options);
            return result;
        } catch (error) {
            throw (error);
        }
    },
    deleteConversation: async function (filter, options) {
        try {
            const client = await this.mongoClient.connect(this.app.get('connectionStrings'));
            const database = client.db("UrWalletPop");
            const collectionName = 'conversations';
            const conversationsCollection = database.collection(collectionName);
            const result = await conversationsCollection.deleteOne(filter, options);
            return result;
        } catch (error) {
            throw (error);
        }
    }
};