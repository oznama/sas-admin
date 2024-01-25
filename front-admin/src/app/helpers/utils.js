import { hide, setMessage } from "../../store/alert/alertSlice";

export const mountMax = 999999;
export const numberMaxLength = 3;
export const taxRate = 0.16;

export const styleInput = { padding: '2px 10px'}
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