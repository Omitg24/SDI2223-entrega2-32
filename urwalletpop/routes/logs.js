module.exports = function (app, logsRepository) {

    /**
     * Metodo que obtiene los logs de la base de datos y si hay una busqueda obtiene los logs en funcion del texto
     * y devuelve una vista que se encargara de mostrarlos
     */
    app.get('/log/list', function (req, res) {
        let options={sort:{date:-1}};
        let filter={};

        //Si la peticion recibe un texto de busqueda actualizamos el filtro
        if(req.query.search != null && typeof(req.query.search) != "undefined" && req.query.search.trim() != "") {
            filter = {"category": req.query.search};
        }
        logsRepository.getLogs(filter,options).then(logs=>{
            let response={
                logList: logs,
            }
            res.render("log/list.twig",response);
        }).catch(error => {
        });
    });
    /**
     * Metodo que elimina todos los logs de la base de datos
     */
    app.get('/log/delete', function (req, res) {
        logsRepository.deleteLogs().then(result => {
            let response={
                deletedCount: result.deletedCount,
            }
            res.render("log/list.twig",response);
        }).catch(error => {
        });
    });
}