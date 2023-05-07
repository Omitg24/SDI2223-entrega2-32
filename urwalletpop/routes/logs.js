module.exports = function (app, logsRepository) {

    /**
     * Metodo que obtiene los logs de la base de datos y si hay una busqueda obtiene los logs en funcion del texto
     * y devuelve una vista que se encargara de mostrarlos
     */
    app.get('/log/list', function (req, res) {
        let filter = {};
        let search = req.query.search || '';

        if (search !== '') {
            filter = {"type": {$regex: new RegExp(search, 'i')}};
        }
        let options = {sort: {date: -1}};
        logsRepository.getLogs(filter, options).then(logs => {
            let response = {
                logList: logs,
            }
            res.render("log/list.twig", response);
        }).catch(error => {
        });
    });
    /**
     * Metodo que elimina todos los logs de la base de datos
     */
    app.get('/log/delete', function (req, res) {
        logsRepository.deleteLogs().then(result => {
            let response = {
                deletedCount: result.deletedCount,
            }
            res.redirect('/log/list');
        }).catch(error => {
        });
    });
}