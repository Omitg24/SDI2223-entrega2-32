module.exports = {
    mongoClient: null,
    app: null,
    /**
     * Método que inicializa el repositorio de usuarios
     * @param app
     * @param mongoClient
     */
    init: function (app, mongoClient) {
        this.mongoClient = mongoClient;
        this.app = app;
    },
    /**
     * Método que obtiene un usuario de la base de datos
     * @param filter
     * @param options
     * @returns {Promise<*>}
     */
    findUser: async function (filter, options) {
        try {
            const client = await this.mongoClient.connect(this.app.get('connectionStrings'));
            const database = client.db("UrWalletPop");
            const collectionName = 'users';
            const usersCollection = database.collection(collectionName);
            const user = await usersCollection.findOne(filter, options);
            return user;
        } catch (error) {
            throw (error);
        }
    },
    /**
     * Método que obtiene todos los usuarios de la base de datos
     * @param filter
     * @param options
     * @returns {Promise<*>}
     */
    getUsers: async function (filter, options) {
        try {
            const client = await this.mongoClient.connect(this.app.get('connectionStrings'));
            const database = client.db("UrWalletPop");
            const collectionName = 'users';
            const usersCollection = database.collection(collectionName);
            const users = await usersCollection.find(filter, options).toArray();
            return users;
        } catch (error) {
            throw (error);
        }
    },
    /**
     * Método que obtiene todos los usuarios de la base de datos de una página en concreto
     * @param filter
     * @param options
     * @param page
     * @returns {Promise<{total: *, users: *}>}
     */
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
    /**
     * Método que inserta un usuario en la base de datos
     * @param user
     * @returns {Promise<any>}
     */
    insertUser: async function (user) {
        try {
            const client = await this.mongoClient.connect(this.app.get('connectionStrings'));
            const database = client.db("UrWalletPop");
            const collectionName = 'users';
            const usersCollection = database.collection(collectionName);
            const result = await usersCollection.insertOne(user);
            return result.insertedId;
        } catch (error) {
            throw (error);
        }
    },
    /**
     * Método que actualiza un usuario en la base de datos
     * @param newUser
     * @param filter
     * @param options
     * @returns {Promise<*>}
     */
    updateUser: async function (newUser, filter, options) {
        try {
            const client = await this.mongoClient.connect(this.app.get('connectionStrings'));
            const database = client.db("UrWalletPop");
            const collectionName = 'users';
            const usersCollection = database.collection(collectionName);
            const result = await usersCollection.updateOne(filter, {$set: newUser}, options);
            return result;
        } catch (error) {
            throw (error);
        }
    },
    /**
     * Método que elimina un usuario de la base de datos
     * @param filter
     * @param options
     * @returns {Promise<*>}
     */
    deleteUsers: async function (filter, options) {
        try {
            const client = await this.mongoClient.connect(this.app.get('connectionStrings'));
            const database = client.db("UrWalletPop");
            const collectionName = 'users';
            const usersCollection = database.collection(collectionName);
            const result = await usersCollection.deleteMany(filter, options);
            return result;
        } catch (error) {
            throw (error);
        }
    }
};