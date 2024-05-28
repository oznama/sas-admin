import { hide, setMessage } from "../../store/alert/alertSlice";

export const mountMax = 99999999;
export const numberMaxLength = 3;
export const taxRate = 0.16;

export const styleTable = { height: '345px' };
export const styleTableRow = { padding: '1px' };
export const styleTableRowBtn = { ...styleTableRow, width: '35px' };

export const handleText = ( { value, maxLength } ) => value.slice(0, maxLength);

export const numberToString = ( number, valueDefault ) => ( (number && number !== undefined) ? number.toString() : valueDefault );

export const upperFirstChar = word => word[0].toUpperCase() + word.substring(1).toLowerCase();

export const upperAllFirtsChars = word => {
    const words = word.split(" ");
    for (let i = 0; i < words.length; i++) {
    words[i] = words[i][0].toUpperCase() + words[i].substr(1);
    }
    words.join(" ");
    return words;
}

export const handleDate = ( date, currentDate ) => {
    const invalidDate = ( date === undefined || date === null || date === '');
    if ( invalidDate ) {
        return currentDate ? new Date() : null;
    }
    return new Date(date);
}

export const handleDateStr = ( strDate ) => {
    const invalidDate = ( strDate === undefined || strDate === null || strDate === '');
    if ( invalidDate ) {
        return null;
    }
    const dateCreated = convertDateToUTC(strDate);
    return dateCreated;
}

const convertDateToUTC = ( strDate ) => {
    const dateParts = strDate.split("/");
    return new Date(+dateParts[2], dateParts[1] - 1, +dateParts[0]); 
};

export const stringToDate = strDate => {
    const dateParts = strDate.split("/");
    // month is 0-based, that's why we need dataParts[1] - 1
    return new Date(+dateParts[2], dateParts[1] - 1, +dateParts[0]); 
}

const buildPayloadMessage = (msg, typ) => ({
    message: msg,
    type: typ
});

export const genericErrorMsg = 'Ha ocurrido un error, favor de contactar al area de sistemas';

export const displayNotification = (dispatch, message, type) => {
    dispatch(setMessage(buildPayloadMessage(message, type)));

    setTimeout(() => {
      dispatch(hide())
    }, 5000);
}

export const getPaymentDate = (currentDate, holyDates) => {
    const paymentDateToAdd = 25;
    let n = 0;
    while (n < paymentDateToAdd) {
        currentDate.setDate(currentDate.getDate() + 1);
        // if( (currentDate.getDay() !== 0 && currentDate.getDay() !== 6) && !holyDates.includes(currentDate) ) {
        n++;
        // }
    }
    /*
     * Days of date
     * Domingo: 0
     * Lunes: 1
     * Martes: 2 
     * Miercoles: 3
     * Jueves: 4, diferencia siguiente miercoles 6
     * Viernes: 5, diferencia siguiente miercoles 5
     * Sabado: 6, diferencias siguiente miercoles 4
     */
    if( currentDate.getDay() !== 3 ) {
        let restDaysForWend = 3 - currentDate.getDay();
        if( restDaysForWend < 0 ) {
            // Is current day is after wednesday in same week
            // go to next week wednesday
            restDaysForWend = 10 - currentDate.getDay();
        }
        currentDate.setDate(currentDate.getDate() + restDaysForWend );
        
    }
    return currentDate;
}

export const formatter = new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'USD',
    minimumFractionDigits: 2
});

export const isNumDec = value => {
    const expression = /^((?!0)\d{1,10}|0|\.\d{1,2})($|\.$|\.\d{1,2}$)/;
    const regex = new RegExp(expression);
    const result = regex.test(value);
    return result;
}

export const linkQueryBuilder = (arr, paramName, boss) => {
    let linkQuery='?';
    boss ? linkQuery+='bossEmail='+boss.replaceAll('@', '%40')+'&' : linkQuery;
    for (let i = 0; i < arr.length; i++) {
        linkQuery+= (i===0 ? '' : '&') + paramName + '=' + arr[i];
    }
    return linkQuery;
}

export const removeCurrencyFormat = value => Number(value.replaceAll(/[^0-9\.-]+/g,""));