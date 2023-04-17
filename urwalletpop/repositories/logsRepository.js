module.exports = {
    mongoClient: null,
    app: null,
    init: function (app, mongoClient) {
        this.mongoClient = mongoClient;
        this.app = app;
    },
    getLogs: async function (filter, options) {
        try {
            const client = await this.mongoClient.connect(this.app.get('connectionStrings'));
            const database = client.db("UrWalletPop");
            const collectionName = 'logs';
            const logsCollection = database.collection(collectionName);
            const logs = await logsCollection.find(filter, options).toArray();
            return logs;
        } catch (error) {
            throw (error);
        }
    },
    insertLog: async function (log) {
        try {
            const client = await this.mongoClient.connect(this.app.get('connectionStrings'));
            const database = client.db("UrWalletPop");
            const collectionName = 'logs';
            const logsCollection = database.collection(collectionName);
            const result = await logsCollection.insertOne(log);
            return result.insertedId;
        } catch (error) {
            throw (error);
        }
    },
    deleteLogs:async function () {
        try {
            const client = await this.mongoClient.connect(this.app.get('connectionStrings'));
            const database = client.db("UrWalletPop");
            const collectionName = 'logs';
            const logsCollection = database.collection(collectionName);
            const result = await logsCollection.deleteMany();
            return result;
        } catch (error) {
            throw (error);
        }
    }
};