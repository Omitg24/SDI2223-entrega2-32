module.exports = function (logsRepository) {
    let defaultType = "PET";
    return (req,res,next) =>{
        let log = {
            date:Date.now(),
            action:req.method,
            url:req.url,
            type:defaultType,
        }
        logsRepository.insertLog(log).catch(error => {
            console.log("No se ha podido registrar la peticion " + req.method)
        });
        next();
    }
}