require 'tty-prompt'
require_relative '../modules/web_service.rb'
require_relative '../modules/validate_user_entry.rb'

# Menu com opções (listagem, pesquisa e compra) para as hospedagens
class AccommodationMenu
    def initialize
        @prompt = TTY::Prompt.new
    end

    # Exibe a tela de menu e as opções
    def display_menu
        while true
            @prompt.say("\n\t→Vagas de hospedagem←")
            chosen_option = @prompt.select("Escolha uma opção:", echo:false) do |menu|
                menu.enum '.'
                menu.choice 'Listar as hospedagens disponíveis', :show_all
                menu.choice 'Pesquisar por hospedagem específica', :search
                menu.choice 'Comprar uma hospedagem', :buy
                menu.choice 'Voltar', :back
            end

            case chosen_option
            when :show_all
                show_accomodations
            when :search
                search_accommodation
            when :buy
                buy_accommodation
            when :back
                return
            end
        end
    end

    private
        # Exibe na tela todas as hospedagens do servidor
        def show_accomodations
            begin
                accommodations = WebService.get_accommodations
            rescue => e
                @prompt.error(e.message)
                return
            end

            accommodations_length = accommodations.length
            if accommodations_length > 0
                @prompt.say("\n#{accommodations_length} hospedagens encontradas")
                @prompt.say("\nListando hospedagens:\n")

                accommodations.each do |accommodation|
                    print(accommodation.string_converter)
                end
            else
                @prompt.say("\nNenhuma hospedagem foi encontrada")
            end
        end

        # Busca por hospedagens semelhantes de acordo com a entrada do usuário
        def search_accommodation
            @prompt.say("Insira os campos a seguir para buscar a hospedagem desejada.")
            hotel_or_city_name = @prompt.ask("Nome do hotel ou da cidade: ", convert: :string) do |e|
                e.required(true, 'Campo vazio, preencha')
            end
            num_rooms = @prompt.ask("Número de quartos: ", convert: :int) do |e|
                e.required true
                e.validate(ValidateUserEntry::INT_REGEX, 'Entrada inválida, insira um número inteiro positivo')
            end
            num_people =  @prompt.ask("Número de pessoas: ", convert: :int) do |e|
                e.required true
                e.validate(ValidateUserEntry::INT_REGEX, 'Entrada inválida, insira um número inteiro positivo')
            end
            entrance_date = @prompt.ask("Data de entrada(dia-mês-ano): ", convert: :date) do |e|
                e.required(true, 'Campo vazio, preencha')
                e.validate(ValidateUserEntry::DATE_REGEX, 'Insira no formato "dia-mês-ano"')
            end
            exit_date = @prompt.ask("Data de saída(dia-mês-ano): ", convert: :date) do |e|
                e.validate(ValidateUserEntry::DATE_REGEX, 'Insira no formado "dia-mês-ano"')
            end

            begin
                similar_accommodations = WebService.get_similar_accommodations(
                    hotel_or_city_name: hotel_or_city_name,
                    entrance_date: entrance_date,
                    exit_date: exit_date,
                    num_rooms: num_rooms,
                    num_people: num_people
                )
            rescue => e
                @prompt.error(e.message)
                return
            end

            similar_accommodations_length = similar_accommodations.length
            if similar_accommodations_length > 0
                @prompt.say("\n#{similar_accommodations_length} hospedagens encontradas")
                
                similar_accommodations.each do |accommodation|
                    print(accommodation.string_converter)
                end
            else
                @prompt.say("\nNenhuma hospedagem foi encontrada")
            end
        end

        # Compra uma hospedagem de acordo com a entrada do usuário
        def buy_accommodation
            @prompt.say("Insira os campos a seguir para comprar uma hospedagem.")
            accommodation_id = @prompt.ask("Identificador da hospedagem: ", convert: :int) do |e|
                e.required true
                e.validate(ValidateUserEntry::INT_REGEX, 'Entrada inválida, insira um número inteiro positivo')
            end

            confirmation = @prompt.yes?("Deseja comprar a hospedagem ##{accommodation_id}?")
            if confirmation == false 
                @prompt.("Compra cancelada")
                return
            end

            begin
                response = WebService.buy_accommodation(accommodation_id)
                @prompt.ok(response)
            rescue => e
                @prompt.error(e.message)
            end
        end

end